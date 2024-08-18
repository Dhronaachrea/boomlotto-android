package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.WithdrawalPendingRequest
import com.skilrock.boomlotto.models.response.WithdrawalPendingResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class WithdrawalPendingRepository {

    suspend fun callWithdrawalPendingApi(withdrawalPendingRequest: WithdrawalPendingRequest) : Response<WithdrawalPendingResponse> {
        return RetrofitNetworking.create().callWithdrawalPendingApi(withdrawalPendingRequest=withdrawalPendingRequest)
    }
}