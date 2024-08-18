package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.BannerRequest
import com.skilrock.boomlotto.models.request.BoomGameInfoRequest
import com.skilrock.boomlotto.models.request.InstantGameListRequest
import com.skilrock.boomlotto.models.response.BannerResponse
import com.skilrock.boomlotto.models.response.BoomGameInfoResponse
import com.skilrock.boomlotto.models.response.InstantGameListResponse
import com.skilrock.boomlotto.models.response.TestAppVersion
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class HomeRepository {

    suspend fun callGameListApi(instantGameListRequest: InstantGameListRequest) : Response<InstantGameListResponse> {
        return RetrofitNetworking.create().callGameListApi(instantGameListRequest)
    }

    suspend fun callBannerApi(bannerRequest: BannerRequest) : Response<BannerResponse> {
        return RetrofitNetworking.create().callBannerApi(bannerRequest)
    }

    suspend fun callGameInfoApi(boomGameInfoRequest: BoomGameInfoRequest) : Response<BoomGameInfoResponse> {
        return RetrofitNetworking.create().callGameInfoApi(boomGameInfoRequest)
    }

    suspend fun callTestVersionApi() : Response<TestAppVersion> {
        return RetrofitNetworking.create().callTestVersionApi()
    }
}