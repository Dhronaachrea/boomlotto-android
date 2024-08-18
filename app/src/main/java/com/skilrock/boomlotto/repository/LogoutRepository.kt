package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.LogOutRequest
import com.skilrock.boomlotto.models.response.LogOutResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class LogoutRepository {

    suspend fun callLogoutApi() : Response<LogOutResponse> {
        return RetrofitNetworking.create().callLogOutApi(LogOutRequest())
    }
}