package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.PlayerProfileRequest
import com.skilrock.boomlotto.models.response.ProfileInfoForPlayerResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class MyProfileForPlayerRepository {
    suspend fun callProfileApiForPlayer() : Response<ProfileInfoForPlayerResponse> {
        return RetrofitNetworking.create().callProfileApiForPlayer(playerProfileRequest = PlayerProfileRequest())
    }
}