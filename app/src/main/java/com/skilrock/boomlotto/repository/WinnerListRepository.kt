package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.AppAnalyticsRequest
import com.skilrock.boomlotto.models.request.WinnerListRequest
import com.skilrock.boomlotto.models.response.AppAnalyticsResponse
import com.skilrock.boomlotto.models.response.WinnerListResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class WinnerListRepository {

    suspend fun callWinnerListApi(winnerListRequest: WinnerListRequest) : Response<WinnerListResponse>{
        return RetrofitNetworking.create().callWinnerListApi(winnerListRequest)
    }

    suspend fun callAppAnalyticsApi(appAnalyticsRequest: AppAnalyticsRequest) : Response<AppAnalyticsResponse>{
        return RetrofitNetworking.create().callAppAnalyticsApi(appAnalyticsRequest)
    }
}