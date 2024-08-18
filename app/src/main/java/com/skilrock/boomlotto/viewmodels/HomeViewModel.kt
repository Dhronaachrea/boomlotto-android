package com.skilrock.boomlotto.viewmodels

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.request.BoomGameInfoRequest
import com.skilrock.boomlotto.models.request.PaymentOptionsRequest
import com.skilrock.boomlotto.models.request.ResultRequest
import com.skilrock.boomlotto.models.request.WinnerListRequest
import com.skilrock.boomlotto.models.response.*
import com.skilrock.boomlotto.network.NoConnectivityException
import com.skilrock.boomlotto.repository.*
import com.skilrock.boomlotto.utility.DEFAULT_RESPONSE_CODE
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.utility.getFormattedAmount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : BaseViewModel() {

    val liveDataBoomGameInfo    = MutableLiveData<ResponseStatus<BoomGameInfoResponse>>()
    val liveDataPaymentOptions  = MutableLiveData<ResponseStatus<JSONObject>>()
    val liveDataLoaderBoomInfo  = MutableLiveData<Boolean>()
    var drawTimer               = MutableLiveData("0 D : 0 H : 0 M : 0 S")
    var jackpotAmount           = MutableLiveData("---")
    var buyAmount               = MutableLiveData("---")
    var productName             = MutableLiveData("---")
    var imageUrl                = MutableLiveData("")
    var drayDay                 = MutableLiveData("")
    val liveDataLogout          = MutableLiveData<ResponseStatus<LogOutResponse>>()
    val liveDateWinnerList      = MutableLiveData<ResponseStatus<WinnerListResponse>>()
    val liveDataResultList      = MutableLiveData<ResponseStatus<ResultResponse>>()
    val liveDataBoomUnitPrice   = MutableLiveData<String>()

    fun callResultApi(startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = ResultRepository().callResultApi(ResultRequest(fromDate = startDate, toDate = endDate, size = 10))
            withContext(Dispatchers.Main) { onApiResponse(liveDataResultList, response) }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateForRequestParam(strDate: String) : String {
        val input   = SimpleDateFormat("MMM dd, yyyy")
        val output  = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            input.parse(strDate)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return ""
    }

    fun callWinnerListApi() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = WinnerListRepository().callWinnerListApi(WinnerListRequest())
            withContext(Dispatchers.Main) { onApiResponse(liveDateWinnerList, response) }
        }
    }

    fun callLogoutAPi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = LogoutRepository().callLogoutApi()
            withContext(Dispatchers.Main) { onApiResponse(liveDataLogout, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callBoomGameInfo() {
        liveDataLoaderBoomInfo.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = HomeRepository().callGameInfoApi(BoomGameInfoRequest())
            withContext(Dispatchers.Main) { onResponseBoomGameInfo(response) }
            liveDataLoaderBoomInfo.postValue(false)
        }
    }

    private fun onResponseBoomGameInfo(responseBoom: Response<BoomGameInfoResponse>) {
        try {
            when {
                responseBoom.isSuccessful && responseBoom.body() != null -> {
                    val boomGameInfoResponse: BoomGameInfoResponse? = responseBoom.body()

                    if (boomGameInfoResponse?.errorCode == 0) {
                        boomGameInfoResponse.data?.games?.dAILYLOTTO?.let { info ->
                            if (info.donation != null && info.donation.isNotEmpty()) {
                                imageUrl.value      = info.donation[0]?.image
                                productName.value   = info.donation[0]?.title ?: "---"
                            }

                            info.content?.let { content ->
                                val cost = content.unitCostJson?.find { cost -> cost?.currency.equals("AED", true) }
                                if (cost != null) {
                                    buyAmount.value     = BuildConfig.CURRENCY_CODE + " " + (cost.price?.let { getFormattedAmount(it) } ?: "--")
                                    liveDataBoomUnitPrice.postValue(BuildConfig.CURRENCY_CODE + " " + (cost.price ?: "-"))
                                    val jackpotAmt      = content.jackpotAmount ?: 0.0
                                    val price           = cost.price ?: 0.0
                                    jackpotAmount.value = BuildConfig.CURRENCY_CODE + " " + getFormattedAmount(jackpotAmt * price)
                                }
                            }
                        }

                        startDrawTimer(boomGameInfoResponse)
                        liveDataBoomGameInfo.postValue(ResponseStatus.Success(boomGameInfoResponse))
                    }
                    else
                        liveDataBoomGameInfo.postValue(ResponseStatus.Error(boomGameInfoResponse?.errorMessage ?: "", boomGameInfoResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                responseBoom.errorBody() != null -> liveDataBoomGameInfo.postValue(ResponseStatus.Error(responseBoom.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataBoomGameInfo.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataBoomGameInfo.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun startDrawTimer(boomGameInfoResponse: BoomGameInfoResponse?) {
        val serverDate      = boomGameInfoResponse?.data?.serverTime?.date
        val drawDate        = boomGameInfoResponse?.data?.games?.dAILYLOTTO?.drawDate//nextDrawDate
        val nextDrawDate    = boomGameInfoResponse?.data?.games?.dAILYLOTTO?.drawDate//nextDrawDate

        nextDrawDate?.let { nextDraw ->
            getAbbreviatedFromDateTime(nextDraw)?.let { drayDay.value = it }
        }

        if (serverDate != null && drawDate != null) {
            val timeDifference = getTimeDifference(drawDate, serverDate)
            if (timeDifference > 0)
                DrawCountDownTimer(timeDifference).start()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getAbbreviatedFromDateTime(dateTime: String): String? {
        val input   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output  = SimpleDateFormat("MMM dd, EEEE")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return null
    }

    inner class DrawCountDownTimer(millisInFuture: Long) :
        CountDownTimer(millisInFuture, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            val totalSeconds: Int   = (millisUntilFinished / 1000).toInt()
            val days: Int           = totalSeconds / (60 * 60 * 24)
            val hours: Int          = (totalSeconds % (60 * 60 * 24)) / 3600
            val totalMinutes: Int   = (totalSeconds % (60 * 60 * 24)) % 3600
            val minutes: Int        = totalMinutes / 60
            val seconds: Int        = totalMinutes % 60

            val drawDay             = if (days < 10) "0$days" else days.toString()
            val drawHour            = if (hours < 10) "0$hours" else hours.toString()
            val drawMinute          = if (minutes < 10) "0$minutes" else minutes.toString()
            val drawSecond          = if (seconds < 10) "0$seconds" else seconds.toString()
            val isDrawDayVisible    = days > 0
            val isDrawHourVisible   = hours > 0

            drawTimer.value = if (isDrawDayVisible && isDrawHourVisible) {
                "$drawDay D : $drawHour H : $drawMinute M : $drawSecond S"
            } else if (isDrawHourVisible) {
                "$drawHour H : $drawMinute M : $drawSecond S"
            } else {
                "$drawMinute M : $drawSecond S"
            }

        }

        override fun onFinish() {
            liveDataVibrator.postValue("")
            callBoomGameInfo()
        }
    }

    private fun getTimeDifference(futureDateStr: String, currentDateStr: String): Long {
        var diff: Long = 0
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val futureDate: Date? = dateFormat.parse(futureDateStr)
            val currentDate: Date? = dateFormat.parse(currentDateStr)
            futureDate?.let { fDate ->
                currentDate?.let { cDate ->
                    diff = fDate.time - cDate.time
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("log", e.message ?: "ERROR")
        }

        Log.w("log", "Home Boom Future Date: $futureDateStr")
        Log.w("log", "Home Boom Current Date: $currentDateStr")
        Log.w("log", "Home Boom Timer Difference: $diff")
        return diff
    }

    fun callPaymentOptionsApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val response: Response<ResponseBody> = DepositRepository().callPaymentOptionsAPI(
                    PaymentOptionsRequest(txnType="WITHDRAWAL")
                )
                withContext(Dispatchers.Main) { onPaymentOptionsResponse(response) }
            } catch (e: NoConnectivityException) {
                liveDataPaymentOptions.postValue(ResponseStatus.TechnicalError(R.string.check_internet_connection))
            } catch (e: Exception) {
                liveDataPaymentOptions.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onPaymentOptionsResponse(response: Response<ResponseBody>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val stringResponse: String? = response.body()?.string()
                    stringResponse?.let { res ->
                        val jsonResponse = JSONObject(res)
                        if (jsonResponse.optInt("errorCode") == 0)
                            liveDataPaymentOptions.postValue(ResponseStatus.Success(jsonResponse))
                        else
                            liveDataPaymentOptions.postValue(ResponseStatus.Error(jsonResponse.optString("errorMessage") ?: "", jsonResponse.optInt("errorCode")))
                    } ?: run {
                        liveDataPaymentOptions.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                    }
                }

                response.errorBody() != null -> liveDataPaymentOptions.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataPaymentOptions.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataPaymentOptions.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun prepareInstantGameUrl(params: InstantGameListResponse.Data.Ige.Engines.DUBAI.Params, game: InstantGameListResponse.Data.Ige.Engines.DUBAI.Game) : String {
        val url = if (PlayerInfo.getPlayerToken().isBlank()) {
            "${params.repo}?root=${params.root}&gameNum=${game.gameNumber}&gameMode=buy&domainName=${params.domainName}&merchantKey=${params.merchantKey}&secureKey=${params.secureKey}&currencyCode=${(params.currencyCode?.get(0) ?: "AED")}&lang=${params.lang}&gameType=scratch&playerId=-&merchantSessionId=-&balance=0&commCharge=0&${System.getProperty("http.agent")}&deviceType=MOBILE&appType=WEB&clientType=FLASH&launchIc=${game.imagePath}&prizeSchemeIge=${convertMapToJson(game.prizeSchemes)}&loaderImage=${convertMapToJson(game.loaderImage)}}&currencyDisplay=${PlayerInfo.getCurrencyDisplayCode()}&merchantCode=WEAVER&name=www.winboom.ae&deviceCheck=false&priceSchemes=${convertMapToJson(game.prizeSchemes)}&prizeSchemeId=7&bonusMultiplier=${game.bonusMultiplier}&productInfo=${Gson().toJson(game.productInfo)}&isNative=android"
        } else {
            "${params.repo}?root=${params.root}&gameNum=${game.gameNumber}&gameMode=buy&domainName=${params.domainName}&merchantKey=${params.merchantKey}&secureKey=${params.secureKey}&currencyCode=${(params.currencyCode?.get(0) ?: "AED")}&lang=${params.lang}&gameType=scratch&playerId=${PlayerInfo.getPlayerId()}&merchantSessionId=${PlayerInfo.getPlayerToken()}&balance=${PlayerInfo.getRawBalance()}&commCharge=0&${System.getProperty("http.agent")}&deviceType=MOBILE&appType=WEB&clientType=FLASH&launchIc=${game.imagePath}&prizeSchemeIge=${convertMapToJson(game.prizeSchemes)}&loaderImage=${convertMapToJson(game.loaderImage)}}&currencyDisplay=${PlayerInfo.getCurrencyDisplayCode()}&merchantCode=WEAVER&name=www.winboom.ae&deviceCheck=false&priceSchemes=${convertMapToJson(game.prizeSchemes)}&prizeSchemeId=7&bonusMultiplier=${game.bonusMultiplier}&productInfo=${Gson().toJson(game.productInfo)}&isNative=android"
        }

        Log.i("log", "Instant Game Url: $url")
        return url
    }

    private fun convertMapToJson(hashMap: LinkedHashMap<String, String>?) : String {
        val jsonObject = JSONObject()
        val entries: List<Pair<String, String>>? = hashMap?.toList()?.map { Pair(it.first, it.second) }

        entries?.forEach { pair ->
            jsonObject.put(pair.first, pair.second)
        }

        Log.d("log", "Map to Json: $jsonObject")
        return jsonObject.toString()
    }

}