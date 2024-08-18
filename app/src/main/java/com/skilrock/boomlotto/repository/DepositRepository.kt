package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.DepositPendingRequest
import com.skilrock.boomlotto.models.request.PaymentOptionsRequest
import com.skilrock.boomlotto.models.response.DepositPendingResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import okhttp3.ResponseBody
import retrofit2.Response

class DepositRepository {

    suspend fun callPaymentOptionsAPI(paymentOptionsRequest: PaymentOptionsRequest) : Response<ResponseBody> {
        return RetrofitNetworking.create().callPaymentOptionsApi(paymentOptionsRequestData=paymentOptionsRequest)
    }

    suspend fun callDepositPendingApi(depositPendingRequest: DepositPendingRequest) : Response<DepositPendingResponse> {
        return RetrofitNetworking.create().callDepositPendingApi(depositPendingRequestData=depositPendingRequest)
    }

}