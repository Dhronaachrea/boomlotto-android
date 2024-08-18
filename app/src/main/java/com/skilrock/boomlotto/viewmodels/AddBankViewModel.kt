package com.skilrock.boomlotto.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.request.AddNewAccountRequest
import com.skilrock.boomlotto.models.request.PaymentOptionsRequest
import com.skilrock.boomlotto.models.request.WithdrawalRequest
import com.skilrock.boomlotto.models.response.AddNewAccountResponse
import com.skilrock.boomlotto.models.response.BankDocUploadResponse
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.WithdrawResponse
import com.skilrock.boomlotto.repository.BankRepository
import com.skilrock.boomlotto.repository.DepositRepository
import com.skilrock.boomlotto.repository.IdVerificationRepository
import com.skilrock.boomlotto.utility.DEFAULT_RESPONSE_CODE
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class AddBankViewModel : BaseViewModel() {

    val liveDataCountry             = MutableLiveData<ResponseStatus<CountryListResponse>>()
    val liveDataAddNewAccResponse   = MutableLiveData<ResponseStatus<AddNewAccountResponse>>()
    val liveDataOtpResponse         = MutableLiveData<ResponseStatus<AddNewAccountResponse>>()
    val liveDataPaymentOptions      = MutableLiveData<ResponseStatus<JSONObject>>()
    val liveDataWithdrawResponse    = MutableLiveData<ResponseStatus<WithdrawResponse>>()
    val liveDataBankDocResponse     = MutableLiveData<ResponseStatus<BankDocUploadResponse>>()

    fun callCountryListApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callCountryListApi()
            withContext(Dispatchers.Main) { onApiResponse(liveDataCountry, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callAddNewAccountApi(requestData: AddNewAccountRequest) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = BankRepository().callAddNewAccountApi(requestData)
            withContext(Dispatchers.Main) { onApiResponse(liveDataAddNewAccResponse, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callOtpApi(requestData: AddNewAccountRequest, isWithdrawalApiCallRequired: Boolean) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = BankRepository().callAddNewAccountApi(requestData)
            withContext(Dispatchers.Main) { onOtpResponse(response, isWithdrawalApiCallRequired) }
            liveDataLoader.postValue(false)
        }
    }

    private fun onOtpResponse(response: Response<AddNewAccountResponse>, isWithdrawalApiCallRequired: Boolean) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val accountResponse: AddNewAccountResponse? = response.body()
                    accountResponse?.isWithdrawalApiCallRequired = isWithdrawalApiCallRequired
                    if (accountResponse?.errorCode == 0)
                        liveDataOtpResponse.postValue(ResponseStatus.Success(accountResponse))
                    else
                        liveDataOtpResponse.postValue(ResponseStatus.Error(accountResponse?.errorMessage ?: "", accountResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveDataOtpResponse.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataOtpResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataOtpResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun callPaymentOptionsApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response: Response<ResponseBody> = DepositRepository().callPaymentOptionsAPI(PaymentOptionsRequest(txnType="WITHDRAWAL"))
            withContext(Dispatchers.Main) { onPaymentOptionsResponse(response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun onPaymentOptionsResponse(response: Response<ResponseBody>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val stringResponse: String? = response.body()?.string()
                    stringResponse?.let { res ->
                        val jsonResponse = JSONObject(res)
                        if (jsonResponse.optInt("errorCode") == 0)
                            liveDataPaymentOptions.postValue(ResponseStatus.Success(jsonResponse))
                        else
                            liveDataPaymentOptions.postValue(ResponseStatus.Error(jsonResponse.optString("errorMessage") ?: "", jsonResponse.optInt("errorCode")))
                    } ?: run {
                        liveDataPaymentOptions.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                    }
                }

                response.errorBody() != null -> liveDataPaymentOptions.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataPaymentOptions.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataPaymentOptions.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun callWithdrawApi(withdrawalRequest: WithdrawalRequest) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = BankRepository().callWithdrawRequestApi(withdrawalRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDataWithdrawResponse, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callPlayerDocUploadApi(accountNumber: String, filePathList: ArrayList<String>, isWithdrawalApiCallRequired: Boolean) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val docRequestParams = bankDocRequestParams(accountNumber, filePathList.size)
            val response = BankRepository().callPlayerDocUploadApi(docRequestParams, imageMultiPartFromFile(filePathList))
            withContext(Dispatchers.Main) { onPlayerDocUploadApiResponse(response, isWithdrawalApiCallRequired) }
            liveDataLoader.postValue(false)
        }
    }

    private fun onPlayerDocUploadApiResponse(response: Response<BankDocUploadResponse>, isWithdrawalApiCallRequired: Boolean) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val docResponse: BankDocUploadResponse? = response.body()
                    docResponse?.isWithdrawalApiCallRequired = isWithdrawalApiCallRequired
                    if (docResponse?.errorCode == 0)
                        liveDataBankDocResponse.postValue(ResponseStatus.Success(docResponse))
                    else
                        liveDataBankDocResponse.postValue(ResponseStatus.Error(docResponse?.errorMessage ?: "", docResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveDataBankDocResponse.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> {
                    liveDataBankDocResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                }
            }
        } catch (e: Exception) {
            liveDataBankDocResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun bankDocRequestParams(accountNumber: String, docCount: Int): HashMap<String, RequestBody> {
        val map             = HashMap<String, RequestBody>()
        map["domainName"]   = RequestBody.create(MediaType.parse("text/plain"), BuildConfig.DOMAIN_NAME)
        map["playerId"]     = RequestBody.create(MediaType.parse("text/plain"), PlayerInfo.getPlayerId().toString())

        for (index in 0 until docCount) {
            map["documents[$index].docType"]     = RequestBody.create(MediaType.parse("text/plain"), "BANK_PROOF")
            map["documents[$index].docName"]     = RequestBody.create(MediaType.parse("text/plain"), "BANK_STATEMENT")
            map["documents[$index].docValue"]    = RequestBody.create(MediaType.parse("text/plain"), accountNumber)
        }
        return map
    }

    private fun imageMultiPartFromFile(filePathList: ArrayList<String>): ArrayList<MultipartBody.Part> {
        val listMultiPart = ArrayList<MultipartBody.Part>()
        filePathList.forEachIndexed { index, path ->
            val file = File(path)
            listMultiPart.add(MultipartBody.Part.createFormData("documents[$index].image", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file)))
        }
        return listMultiPart
    }
}