package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.request.PaymentOptionsRequest
import com.skilrock.boomlotto.repository.DepositRepository
import com.skilrock.boomlotto.utility.DEFAULT_RESPONSE_CODE
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class MyAccountViewModel : BaseViewModel() {

    val liveDataPaymentOptions  = MutableLiveData<ResponseStatus<JSONObject>>()

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

}