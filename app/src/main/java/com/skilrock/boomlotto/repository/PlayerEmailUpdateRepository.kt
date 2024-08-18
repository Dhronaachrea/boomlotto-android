package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.EmailOtpRequest
import com.skilrock.boomlotto.models.request.EmailVerificationRequest
import com.skilrock.boomlotto.models.response.EmailOtpResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class PlayerEmailUpdateRepository {

    suspend fun callEmailOtpApi(emailOtpRequest: EmailOtpRequest) : Response<EmailOtpResponse> {
        return RetrofitNetworking.create().callEmailOtpApi(emailOtpRequest = emailOtpRequest)
    }

    suspend fun callEmailVerificationApi(emailVerificationRequest: EmailVerificationRequest) : Response<EmailOtpResponse>{
        return RetrofitNetworking.create().callEmailVerificationApi(emailVerificationRequest = emailVerificationRequest)
    }

}