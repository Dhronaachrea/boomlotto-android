package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.LoginRequest
import com.skilrock.boomlotto.models.request.ReferCodeRequest
import com.skilrock.boomlotto.models.response.*
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class LoginRepository {

    suspend fun callOtpApi(mobileNumber: String) : Response<LoginOtpResponse> {
        return RetrofitNetworking.create().callLoginOtpApi(mobileNo = mobileNumber)
    }

    suspend fun callLoginApi(loginRequest: LoginRequest) : Response<LoginResponse> {
        return RetrofitNetworking.create().callLoginApi(loginRequest)
    }

    suspend fun callCountryListApi() : Response<CountryListResponse> {
        return RetrofitNetworking.create().callCountryListApi()
    }

    suspend fun callReferCodeApi(referCodeRequest: ReferCodeRequest) : Response<ReferCodeResponse> {
        return RetrofitNetworking.create().callReferCodeApi(referCodeRequest)
    }

    suspend fun <T: AppResponse> handleRequest(requestFunc: suspend () -> T): Result<T> {
        return try {
            Result.success(requestFunc.invoke())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}