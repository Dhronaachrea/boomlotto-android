package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.MyTransactionsRequest
import com.skilrock.boomlotto.models.response.MyTransactionsResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class MyTransactionRepository {

    suspend fun callTransactionApi(myTransactionsRequest: MyTransactionsRequest) : Response<MyTransactionsResponse> {
        return RetrofitNetworking.create().callMyTransactionApi(myTransactionsRequest)
    }
}