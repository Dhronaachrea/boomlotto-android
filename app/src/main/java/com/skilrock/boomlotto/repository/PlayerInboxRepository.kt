package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.PlayerInboxDeleteRequest
import com.skilrock.boomlotto.models.request.PlayerInboxReadRequest
import com.skilrock.boomlotto.models.request.PlayerInboxRequest
import com.skilrock.boomlotto.models.response.PlayerInboxReadResponse
import com.skilrock.boomlotto.models.response.PlayerInboxResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class PlayerInboxRepository {

    suspend fun callPlayerInboxApi(playerInboxRequest: PlayerInboxRequest) : Response<PlayerInboxResponse> {
        return RetrofitNetworking.create().callPlayerInboxApi(playerInboxRequest)
    }

    suspend fun callInboxReadApi(playerInboxReadRequestData: PlayerInboxReadRequest) : Response<PlayerInboxReadResponse> {
        return RetrofitNetworking.create().callInboxReadApi(playerInboxReadRequestData)
    }

    suspend fun callInboxDeleteApi(playerInboxDeleteRequestData: PlayerInboxDeleteRequest) : Response<PlayerInboxResponse> {
        return RetrofitNetworking.create().callInboxDeleteApi(playerInboxDeleteRequestData)
    }
}