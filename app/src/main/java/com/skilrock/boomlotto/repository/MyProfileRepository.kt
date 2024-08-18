package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.PlayerProfileRequest
import com.skilrock.boomlotto.models.response.PlayerProfileResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class MyProfileRepository {
    suspend fun callPlayerProfileApi() : Response<PlayerProfileResponse> {
        return RetrofitNetworking.create().callPlayerProfileApi(playerProfileRequest = PlayerProfileRequest())
    }
}