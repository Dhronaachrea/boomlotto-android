package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.MyTicketDetailRequest
import com.skilrock.boomlotto.models.request.ServerTimeRequest
import com.skilrock.boomlotto.models.response.InstantGameTicketListResponse
import com.skilrock.boomlotto.models.response.MyTicketDetailResponse
import com.skilrock.boomlotto.models.response.MyTicketsListResponse
import com.skilrock.boomlotto.models.response.ServerTimeResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class MyTicketRepository {

    suspend fun callTicketDetailApi(myTicketDetailRequest: MyTicketDetailRequest) : Response<MyTicketDetailResponse> {
        return RetrofitNetworking.create().callTicketDetailApi(myTicketDetailRequest)
    }

    suspend fun callTicketListApi() : Response<MyTicketsListResponse> {
        return RetrofitNetworking.create().callTicketListApi()
    }

    suspend fun callInstantGameTicketList() : Response<InstantGameTicketListResponse> {
        return RetrofitNetworking.create().callInstantGameTicketList()
    }

    suspend fun callServerTimeApi(serverTimeRequest: ServerTimeRequest) : Response<ServerTimeResponse> {
        return RetrofitNetworking.create().callServerTimeApi(serverTimeRequest)
    }
}