package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.VersionControlRequest
import com.skilrock.boomlotto.models.response.VersionControlResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class SplashRepository {

    suspend fun callVersionControlApi(versionControlRequest: VersionControlRequest) : Response<VersionControlResponse> {
        return RetrofitNetworking.create().callVersionControlAPi(versionControlRequest)
    }

}