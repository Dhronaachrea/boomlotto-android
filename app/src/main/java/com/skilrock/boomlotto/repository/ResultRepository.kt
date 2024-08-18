package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.ResultRequest
import com.skilrock.boomlotto.models.response.ResultResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class ResultRepository {

    suspend fun callResultApi(resultRequest: ResultRequest) : Response<ResultResponse> {
        return RetrofitNetworking.create().callResultApi(resultRequest)
    }
}