package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.BoomLottoBuyTicketRequest
import com.skilrock.boomlotto.models.request.BoomLottoGameRequest
import com.skilrock.boomlotto.models.response.BoomLottoBuyTicketResponse
import com.skilrock.boomlotto.models.response.BoomLottoGameResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import retrofit2.Response

class BoomLottoRepository {

    suspend fun callBoomLottoGameApi(boomLottoGameRequest: BoomLottoGameRequest) : Response<BoomLottoGameResponse> {
        return RetrofitNetworking.create().callBoomLottoGameApi(boomLottoGameRequest)
    }

    suspend fun callBoomLottoBuyTicketApi(boomLottoBuyTicketRequest: BoomLottoBuyTicketRequest) : Response<BoomLottoBuyTicketResponse> {
        return RetrofitNetworking.create().callBoomLottoBuyTicketApi(boomLottoBuyTicketRequest)
    }

}