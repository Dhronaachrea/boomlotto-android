package com.skilrock.boomlotto.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.response.CityResponse
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.IdVerificationResponse
import com.skilrock.boomlotto.models.response.StateResponse
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
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat


class WithdrawIdVerificationViewModel : BaseViewModel() {

    val liveDataCity            = MutableLiveData<ResponseStatus<ArrayList<CityResponse.Data?>>>()
    val liveDataState           = MutableLiveData<ResponseStatus<ArrayList<StateResponse.Data?>>>()
    val liveDataCountry         = MutableLiveData<ResponseStatus<CountryListResponse>>()
    val liveDataIdVerification  = MutableLiveData<ResponseStatus<IdVerificationResponse>>()

    fun callCountryListApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callCountryListApi()
            withContext(Dispatchers.Main) { onApiResponse(liveDataCountry, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callIdVerificationWithdrawalApi(firstName: String, lastName: String, dob: String, gender: String, nationality: String, docName: String,
                                        docValue: String, expiryDate: String, country: String, state: String, city: String, filePathList: ArrayList<String>) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val verificationRequestParams = verificationRequestParams(firstName, lastName, dob, gender, nationality, docName, docValue, expiryDate, country, state, city, filePathList.size)
            val response = IdVerificationRepository().callIdVerificationWithdrawalApi(verificationRequestParams, imageMultiPartFromFile(filePathList))
            withContext(Dispatchers.Main) { onApiResponse(liveDataIdVerification, response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun verificationRequestParams(firstName: String, lastName: String, dob: String, gender: String, nationality: String,
                                          docName: String, docValue: String, expiryDate: String, country: String, state: String, city: String, docCount: Int): HashMap<String, RequestBody> {
        val map = HashMap<String, RequestBody>()
        map["merchantPlayerId"]         = RequestBody.create(MediaType.parse("text/plain"), PlayerInfo.getPlayerId().toString())
        map["domainName"]               = RequestBody.create(MediaType.parse("text/plain"), BuildConfig.DOMAIN_NAME)
        map["firstName"]                = RequestBody.create(MediaType.parse("text/plain"), firstName)
        map["lastName"]                 = RequestBody.create(MediaType.parse("text/plain"), lastName)
        map["dob"]                      = RequestBody.create(MediaType.parse("text/plain"), getFormattedDob(dob))
        map["gender"]                   = RequestBody.create(MediaType.parse("text/plain"), gender)
        map["nationality"]              = RequestBody.create(MediaType.parse("text/plain"), nationality)
        map["country"]                  = RequestBody.create(MediaType.parse("text/plain"), country)
        map["stateCode"]                = RequestBody.create(MediaType.parse("text/plain"), state)
        map["city"]                     = RequestBody.create(MediaType.parse("text/plain"), city)

        for (index in 0 until docCount) {
            map["documents[$index].docType"]    = RequestBody.create(MediaType.parse("text/plain"), "ID_PROOF")
            map["documents[$index].docName"]    = RequestBody.create(MediaType.parse("text/plain"), docName)
            map["documents[$index].docValue"]   = RequestBody.create(MediaType.parse("text/plain"), docValue)
            map["documents[$index].docExpiry"]  = RequestBody.create(MediaType.parse("text/plain"), getFormattedExpiryDate(expiryDate))
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

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDob(dob: String) : String {
        return try {
            val parser      = SimpleDateFormat("MMM dd, yyyy")
            val formatter   = SimpleDateFormat("yyyy-MM-dd")
            parser.parse(dob)?.let { formatter.format(it) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedExpiryDate(expiryDate: String) : String {
        return try {
            val parser      = SimpleDateFormat("MMM dd, yyyy")
            val formatter   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            parser.parse(expiryDate)?.let { formatter.format(it) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun callStateListApi(countryCode: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callStateListApi(countryCode)
            withContext(Dispatchers.Main) { onStateListResponse(response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun onStateListResponse(response: Response<StateResponse>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val stateListResponse: StateResponse? = response.body()
                    if (stateListResponse?.errorCode == 0) {
                        if (stateListResponse.data != null && stateListResponse.data.isNotEmpty())
                            liveDataState.postValue(ResponseStatus.Success(stateListResponse.data))
                        else
                            liveDataState.postValue(ResponseStatus.TechnicalError(R.string.no_state_found))
                    }
                    else
                        liveDataState.postValue(ResponseStatus.Error(stateListResponse?.errorMessage ?: "", stateListResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveDataState.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataState.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataState.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun callCityListApi(stateCode: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callCityListApi(stateCode)
            withContext(Dispatchers.Main) { onCityListResponse(response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun onCityListResponse(response: Response<CityResponse>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val cityListResponse: CityResponse? = response.body()
                    if (cityListResponse?.errorCode == 0) {
                        if (cityListResponse.data != null && cityListResponse.data.isNotEmpty())
                            liveDataCity.postValue(ResponseStatus.Success(cityListResponse.data))
                        else
                            liveDataCity.postValue(ResponseStatus.TechnicalError(R.string.no_city_found))
                    }
                    else
                        liveDataCity.postValue(ResponseStatus.Error(cityListResponse?.errorMessage ?: "", cityListResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveDataCity.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataCity.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataCity.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

}