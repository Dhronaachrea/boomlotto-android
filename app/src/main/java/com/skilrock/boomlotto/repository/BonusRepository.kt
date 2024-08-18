package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.BonusRequest
import com.skilrock.boomlotto.models.response.BonusResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class BonusRepository {

    suspend fun callBonusApi(bonusRequestDetails: BonusRequest) : Response<BonusResponse> {
        return RetrofitNetworking.create().callCheckBonusStatusPlayerApi(bonusInfoRequest = bonusRequestDetails)
    }
}