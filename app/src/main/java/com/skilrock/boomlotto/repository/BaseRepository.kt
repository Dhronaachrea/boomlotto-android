package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.HeaderInfoRequest
import com.skilrock.boomlotto.models.response.HeaderInfoResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class BaseRepository {

    suspend fun callHeaderInfoApi() : Response<HeaderInfoResponse> {
        return RetrofitNetworking.create().callHeaderInfoApi(HeaderInfoRequest())
    }

}