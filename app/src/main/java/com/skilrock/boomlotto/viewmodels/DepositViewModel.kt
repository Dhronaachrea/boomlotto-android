package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.request.BonusRequest
import com.skilrock.boomlotto.models.request.DepositPendingRequest
import com.skilrock.boomlotto.models.request.PaymentOptionsRequest
import com.skilrock.boomlotto.models.response.BonusResponse
import com.skilrock.boomlotto.models.response.DepositPendingResponse
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse
import com.skilrock.boomlotto.repository.BonusRepository
import com.skilrock.boomlotto.repository.DepositRepository
import com.skilrock.boomlotto.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class DepositViewModel : BaseViewModel() {

    val liveDataPaymentOptions  = MutableLiveData<ResponseStatus<JSONObject>>()
    val liveDataPendingResponse = MutableLiveData<ResponseStatus<DepositPendingResponse>>()
    val liveDataBonus           = MutableLiveData<ResponseStatus<BonusResponse>>()

    fun callBonusApi(txnId: Long) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = BonusRepository().callBonusApi(BonusRequest(txnId = txnId))
            withContext(Dispatchers.Main) { onApiResponse(liveDataBonus, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callPaymentOptionsApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response: Response<ResponseBody> = DepositRepository().callPaymentOptionsAPI(PaymentOptionsRequest(txnType="DEPOSIT"))
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

    fun callDepositPendingApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = DepositRepository().callDepositPendingApi(DepositPendingRequest(txnType="DEPOSIT"))
            withContext(Dispatchers.Main) { onApiResponse(liveDataPendingResponse, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun getFormData(data: PaymentOptionsResponse.PayTypeMap, amount: String): String {
        val userAgent = System.getProperty("http.agent")
        return "<html>" +
                "<body onload='document.forms[\"payment_form\"].submit()'>" +
                "<form action='${DEPOSIT_REQUEST_URL}' method='post' name='payment_form' id='form-submit'>" +
                "  <input type='hidden' name='paymentTypeId' value='${data.payTypeId}'/>" +
                "  <input type='hidden' name='txnType' value='DEPOSIT'/>" +
                "  <input type='hidden' name='paymentTypeCode' value='${data.payTypeCode}'/>" +
                "  <input type='hidden' name='amount' value='${amount}'/>" +
                "  <input type='hidden' name='domainName' value='${BuildConfig.DOMAIN_NAME}'/>" +
                "  <input type='hidden' name='currencyCode' value='${data.currencyMap?.get(0)?.value}'/>" +
                "  <input type='hidden' name='subTypeId' value='${data.subTypeCode}'/>" +
                "  <input type='hidden' name='deviceType' value='${DEVICE_TYPE}'/>" +
                "  <input type='hidden' name='userAgent' value='${userAgent}'/>" +
                "  <input type='hidden' name='playerId' value='${PlayerInfo.getPlayerId()}'/>" +
                "  <input type='hidden' name='playerToken' value='${PlayerInfo.getPlayerToken()}'/>" +
                "  <input type='hidden' name='merchantCode' value='infiniti'/>" +
                "  <input type='hidden' name='respSuccess' value='MOBILE_APP'/>" +
                "  <input type='hidden' name='respError' value='MOBILE_APP'/>" +
                "</form>" +
                "</body>" +
                "</html>"
    }

}