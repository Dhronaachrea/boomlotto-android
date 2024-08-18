package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.request.AppAnalyticsRequest
import com.skilrock.boomlotto.models.request.WinnerListRequest
import com.skilrock.boomlotto.models.response.AppAnalyticsResponse
import com.skilrock.boomlotto.models.response.WinnerListResponse
import com.skilrock.boomlotto.repository.WinnerListRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WinnerListViewModel : BaseViewModel() {

    val liveDateWinnerList      = MutableLiveData<ResponseStatus<WinnerListResponse>>()
    val liveDateAppAnalytics    = MutableLiveData<ResponseStatus<AppAnalyticsResponse>>()

    fun callWinnerListApi() {
        //liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = WinnerListRepository().callWinnerListApi(WinnerListRequest())
            withContext(Dispatchers.Main) { onApiResponse(liveDateWinnerList, response) }
            //liveDataLoader.postValue(false)
        }
    }

    fun callAppAnalyticsApi(appAnalyticsRequest: AppAnalyticsRequest) {

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = WinnerListRepository().callAppAnalyticsApi(appAnalyticsRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDateAppAnalytics, response) }
        }
    }

}