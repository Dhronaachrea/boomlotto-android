package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.request.BannerRequest
import com.skilrock.boomlotto.models.request.InstantGameListRequest
import com.skilrock.boomlotto.models.response.BannerResponse
import com.skilrock.boomlotto.models.response.InstantGameListResponse
import com.skilrock.boomlotto.models.response.TestAppVersion
import com.skilrock.boomlotto.repository.HomeRepository
import com.skilrock.boomlotto.utility.DEFAULT_RESPONSE_CODE
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SplashViewModel : BaseViewModel() {

    //val liveDataVersionControl    = MutableLiveData<ResponseStatus<VersionControlResponse>>()
    val liveDataBanner              = MutableLiveData<ResponseStatus<BannerResponse>>()
    val liveDataInstantGameList     = MutableLiveData<ResponseStatus<InstantGameListResponse>>()
    val liveDataTestVersion         = MutableLiveData<Pair<Boolean, String>>()

    fun callBannerApi(bannerRequest: BannerRequest) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = HomeRepository().callBannerApi(bannerRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDataBanner, response) }
        }
    }

    fun callTestVersionApi() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response: Response<TestAppVersion> = HomeRepository().callTestVersionApi()
            withContext(Dispatchers.Main) {
                onTestVersionResponse(response)
            }
        }
    }

    private fun onTestVersionResponse(response: Response<TestAppVersion>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val apiResponse: TestAppVersion? = response.body()
                    if (apiResponse != null) {
                        val android = apiResponse.android
                        val version = android?.appVersion
                        if (android != null && version != null) {
                            if (BuildConfig.VERSION_NAME != version) {
                                val message = android.message ?: "New version is available for download. Please download latest version from your email sent by Firebase."
                                liveDataTestVersion.postValue(Pair(true, message))
                            } else
                                liveDataTestVersion.postValue(Pair(false, ""))
                        } else
                            liveDataTestVersion.postValue(Pair(false, ""))
                    } else {
                        liveDataTestVersion.postValue(Pair(false, ""))
                    }
                }

                response.errorBody() != null -> liveDataTestVersion.postValue(Pair(false, ""))

                else -> liveDataTestVersion.postValue(Pair(false, ""))
            }
        } catch (e: Exception) {
            liveDataTestVersion.postValue(Pair(false, ""))
        }
    }

    fun callGameListApi() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = HomeRepository().callGameListApi(InstantGameListRequest())
            withContext(Dispatchers.Main) { onResponseGameList(response) }
        }
    }

    private fun onResponseGameList(responseInstant: Response<InstantGameListResponse>) {
        try {
            when {
                responseInstant.isSuccessful && responseInstant.body() != null -> {
                    val response: InstantGameListResponse? = responseInstant.body()
                    if (response?.success == true)
                        liveDataInstantGameList.postValue(ResponseStatus.Success(response))
                    else
                        liveDataInstantGameList.postValue(ResponseStatus.Error(response?.errorMessage ?: "", response?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                responseInstant.errorBody() != null -> liveDataInstantGameList.postValue(ResponseStatus.Error(responseInstant.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataInstantGameList.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataInstantGameList.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    /*fun callVersionControlApi(versionControlRequest: VersionControlRequest) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = SplashRepository().callVersionControlApi(versionControlRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDataVersionControl, response) }
        }
    }*/
}
