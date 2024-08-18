package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.response.AppResponse
import com.skilrock.boomlotto.models.response.HeaderInfoResponse
import com.skilrock.boomlotto.network.NoConnectivityException
import com.skilrock.boomlotto.repository.BaseRepository
import com.skilrock.boomlotto.utility.DEFAULT_RESPONSE_CODE
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.utility.ResponseStatusError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class BaseViewModel : ViewModel() {

    val liveDataLoader              = MutableLiveData<Boolean>()
    val liveDataVibrator            = MutableLiveData<String>()
    val liveDataHideKeyboard        = MutableLiveData<Boolean>()
    val liveDataHeaderInfo          = MutableLiveData<ResponseStatus<HeaderInfoResponse>>()
    val liveDataHeaderInfoCallback  = MutableLiveData<ResponseStatus<HeaderInfoResponse>>()
    val liveDataNetworkError        = MutableLiveData<ResponseStatusError>()

    val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        liveDataLoader.postValue(false)
        if (throwable is NoConnectivityException)
            liveDataNetworkError.postValue(ResponseStatusError.NoInternetError(R.string.check_internet_connection))
        else
            liveDataNetworkError.postValue(ResponseStatusError.TechnicalError(R.string.some_technical_issue))
    }

    fun callHeaderInfoApi(apiCalledSourceTag: String = "HOME") {
        if (apiCalledSourceTag != "HOME")
            liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = BaseRepository().callHeaderInfoApi()
                withContext(Dispatchers.Main) { onHeaderInfoApiResponse(response, apiCalledSourceTag) }
            } catch (e: NoConnectivityException) {
                liveDataHeaderInfo.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
                if (apiCalledSourceTag != "HOME")
                    liveDataHeaderInfoCallback.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
            } catch (e: Exception) {
                liveDataHeaderInfo.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                if (apiCalledSourceTag != "HOME")
                    liveDataHeaderInfoCallback.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                if (apiCalledSourceTag != "HOME")
                    liveDataLoader.postValue(false)
            }
        }
    }

    private fun onHeaderInfoApiResponse(detailResponse: Response<HeaderInfoResponse>, apiCalledSourceTag: String) {
        try {
            when {
                detailResponse.isSuccessful && detailResponse.body() != null -> {
                    val responseHeaderInfo: HeaderInfoResponse? = detailResponse.body()
                    if (responseHeaderInfo?.errorCode == 0) {
                        liveDataHeaderInfo.postValue(ResponseStatus.Success(responseHeaderInfo))
                        if (apiCalledSourceTag != "HOME") {
                            responseHeaderInfo.apiCalledSourceTag = apiCalledSourceTag
                            liveDataHeaderInfoCallback.postValue(ResponseStatus.Success(responseHeaderInfo))
                        }
                    } else {
                        liveDataHeaderInfo.postValue(ResponseStatus.Error(responseHeaderInfo?.respMsg ?: "", responseHeaderInfo?.errorCode ?: DEFAULT_RESPONSE_CODE))
                        if (apiCalledSourceTag != "HOME")
                            liveDataHeaderInfoCallback.postValue(ResponseStatus.Error(responseHeaderInfo?.respMsg ?: "", responseHeaderInfo?.errorCode ?: DEFAULT_RESPONSE_CODE))
                    }
                }

                detailResponse.errorBody() != null -> {
                    liveDataHeaderInfo.postValue(ResponseStatus.Error(detailResponse.errorBody().toString(), DEFAULT_RESPONSE_CODE))
                    if (apiCalledSourceTag != "HOME")
                        liveDataHeaderInfoCallback.postValue(ResponseStatus.Error(detailResponse.errorBody().toString(), DEFAULT_RESPONSE_CODE))
                }

                else -> {
                    liveDataHeaderInfo.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                    if (apiCalledSourceTag != "HOME")
                        liveDataHeaderInfoCallback.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                }
            }
        } catch (e: Exception) {
            liveDataHeaderInfo.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
            liveDataHeaderInfoCallback.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun <T: AppResponse> onApiResponse(liveData: MutableLiveData<ResponseStatus<T>>, response: Response<T>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val apiResponse: T? = response.body()
                    if (apiResponse?.errorCode == 0)
                        liveData.postValue(ResponseStatus.Success(apiResponse))
                    else
                        liveData.postValue(ResponseStatus.Error(apiResponse?.errorMessage ?: "", apiResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveData.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveData.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveData.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

}