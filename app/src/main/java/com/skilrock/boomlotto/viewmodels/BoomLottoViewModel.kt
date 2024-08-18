package com.skilrock.boomlotto.viewmodels

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.request.BoomLottoBuyTicketRequest
import com.skilrock.boomlotto.models.request.BoomLottoGameRequest
import com.skilrock.boomlotto.models.response.BoomLottoBuyTicketResponse
import com.skilrock.boomlotto.models.response.BoomLottoGameResponse
import com.skilrock.boomlotto.repository.BoomLottoRepository
import com.skilrock.boomlotto.utility.BOOM_GAME_CODE
import com.skilrock.boomlotto.utility.DEFAULT_RESPONSE_CODE
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.utility.getFormattedAmount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BoomLottoViewModel : BaseViewModel() {

    val liveDataBoomGame                = MutableLiveData<ResponseStatus<BoomLottoGameResponse>>()
    val liveDataBuyTicket               = MutableLiveData<ResponseStatus<BoomLottoBuyTicketResponse>>()
    val liveDataGameNumbersList         = MutableLiveData<ArrayList<BoomLottoGameResponse.ResponseData.GameRespVO.NumberConfig.Range.Ball?>>()
    val liveDataHurryUp                 = MutableLiveData<Boolean>()

    val currencyCode                    = MutableLiveData(BuildConfig.CURRENCY_CODE)

    var jackpotAmount                   = MutableLiveData("---")
    var drawTimer                       = MutableLiveData("0 D : 0 H : 0 M : 0 S")
    var drayDay                         = MutableLiveData("")
    var imageUrl                        = MutableLiveData("")
    var donationItemName                = MutableLiveData("")

    var mIsQuickPickAllowed             = MutableLiveData(true)
    var mUnitPriceLiveData              = MutableLiveData("0.0")
    var mMaximumSelectionLimitLiveData  = MutableLiveData("0")

    var mUnitPrice                      = 0.0
    var mMaxPanelAllowed                = 0
    private var currentTime             = ""
    var mMaximumSelectionLimit          = 0
    private var isHurryUpAllowed        = true

    lateinit var mDrawList: List<BoomLottoGameResponse.ResponseData.GameRespVO.DrawRespVO?>
    lateinit var mBoomResponse: BoomLottoGameResponse.ResponseData.GameRespVO

    fun callBoomLottoGameApi(isDrawCompleted: Boolean) {
        //val dummyJsonResponse = "{'responseCode':0,'responseMessage':'Success','responseData':{'gameRespVOs':[{'id':3,'gameNumber':3,'gameName':'Daily Lotto','gameCode':'DailyLotto','betLimitEnabled':'NO','familyCode':'StandardLottoKeno','lastDrawResult':'01,02,03,04,05','displayOrder':'3','drawFrequencyType':\"\",'timeToFetchUpdatedGameInfo':'2021-09-23 09:14:45','numberConfig':{'range':[{'ball':[{'color':\"\",'label':\"\",'number':'01'},{'color':\"\",'label':\"\",'number':'02'},{'color':\"\",'label':\"\",'number':'03'},{'color':\"\",'label':\"\",'number':'04'},{'color':\"\",'label':\"\",'number':'05'},{'color':\"\",'label':\"\",'number':'06'},{'color':\"\",'label':\"\",'number':'07'},{'color':\"\",'label':\"\",'number':'08'},{'color':\"\",'label':\"\",'number':'09'},{'color':\"\",'label':\"\",'number':'10'},{'color':\"\",'label':\"\",'number':'11'},{'color':\"\",'label':\"\",'number':'12'},{'color':\"\",'label':\"\",'number':'13'},{'color':\"\",'label':\"\",'number':'14'},{'color':\"\",'label':\"\",'number':'15'},{'color':\"\",'label':\"\",'number':'16'},{'color':\"\",'label':\"\",'number':'17'},{'color':\"\",'label':\"\",'number':'18'},{'color':\"\",'label':\"\",'number':'19'},{'color':\"\",'label':\"\",'number':'20'},{'color':\"\",'label':\"\",'number':'21'},{'color':\"\",'label':\"\",'number':'22'},{'color':\"\",'label':\"\",'number':'23'},{'color':\"\",'label':\"\",'number':'24'},{'color':\"\",'label':\"\",'number':'25'},{'color':\"\",'label':\"\",'number':'26'},{'color':\"\",'label':\"\",'number':'27'},{'color':\"\",'label':\"\",'number':'28'},{'color':\"\",'label':\"\",'number':'29'},{'color':\"\",'label':\"\",'number':'30'}]}]},'betRespVOs':[{'unitPrice':20.0,'maxBetAmtMul':10,'betDispName':'Pick5','betCode':'Direct5','betName':'Direct5','betGroup':null,'pickTypeData':{'pickType':[{'name':'Manual','code':'Direct5','range':[{'pickMode':'Simple','pickCount':'5,5','pickValue':\"\",'pickConfig':'Number','qpAllowed':'YES'}],'coordinate':null,'description':'Select any 5 numbers from a pool of 1 to 30.'}]},'inputCount':'5','winMode':'MAIN','betOrder':1}],'drawRespVOs':[{'drawId':3677,'drawName':'Daily Lotto','drawDay':'THURSDAY','drawDateTime':'2021-09-23 09:15:00','drawSaleStartTime':'2021-09-22 21:39:45','drawFreezeTime':'2021-09-23 09:14:55','drawSaleStopTime':'2021-09-23 09:14:45','drawStatus':'ACTIVE'},{'drawId':3678,'drawName':'Daily Lotto','drawDay':'THURSDAY','drawDateTime':'2021-09-23 09:20:00','drawSaleStartTime':'2021-09-22 21:44:45','drawFreezeTime':'2021-09-23 09:19:55','drawSaleStopTime':'2021-09-23 09:19:45','drawStatus':'ACTIVE'},{'drawId':3679,'drawName':'Daily Lotto','drawDay':'THURSDAY','drawDateTime':'2021-09-23 09:25:00','drawSaleStartTime':'2021-09-22 21:49:45','drawFreezeTime':'2021-09-23 09:24:55','drawSaleStopTime':'2021-09-23 09:24:45','drawStatus':'ACTIVE'}],'nativeCurrency':'AED','drawEvent':'SHARED_DRAW','gameStatus':'SALE_OPEN','gameOrder':'3','consecutiveDraw':'1,2,3,4,5','maxAdvanceDraws':5,'drawPrizeMultiplier':{'createEvent':null,'applyOnBet':'MAIN','multiplier':null},'lastDrawFreezeTime':\"\",'lastDrawDateTime':\"\",'lastDrawSaleStopTime':\"\",'lastDrawTime':'2021-09-23 09:05:00','ticket_expiry':90,'lastDrawWinningResultVOs':[{'lastDrawDateTime':'2021-09-23 09:05:00','winningNumber':'01,02,03,04,05','winningMultiplierInfo':{'multiplierCode':null,'value':null},'runTimeFlagInfo':[],'sideBetMatchInfo':[],'drawId':0,'currentDrawStatus':null},{'lastDrawDateTime':'2021-09-22 21:55:00','winningNumber':'12,01,19,30,14','winningMultiplierInfo':{'multiplierCode':null,'value':null},'runTimeFlagInfo':[],'sideBetMatchInfo':[],'drawId':0,'currentDrawStatus':null},{'lastDrawDateTime':'2021-09-22 21:50:00','winningNumber':'25,24,17,06,19','winningMultiplierInfo':{'multiplierCode':null,'value':null},'runTimeFlagInfo':[],'sideBetMatchInfo':[],'drawId':0,'currentDrawStatus':null},{'lastDrawDateTime':'2021-09-22 21:45:00','winningNumber':'26,13,27,10,15','winningMultiplierInfo':{'multiplierCode':null,'value':null},'runTimeFlagInfo':[],'sideBetMatchInfo':[],'drawId':0,'currentDrawStatus':null},{'lastDrawDateTime':'2021-09-22 21:40:00','winningNumber':'12,26,20,28,23','winningMultiplierInfo':{'multiplierCode':null,'value':null},'runTimeFlagInfo':[],'sideBetMatchInfo':[],'drawId':0,'currentDrawStatus':null}],'maxPanelAllowed':10,'gameSchemas':{'gameDevName':'DailyLotto','matchDetail':[{'slabInfo':null,'match':'Match 1','rank':1,'type':'PARIMUTUEL','prizeAmount':'5000.0','betType':'Direct5','pattern':null},{'slabInfo':null,'match':'Match 2','rank':2,'type':'FIXED','prizeAmount':'50.0','betType':'Direct5','pattern':null},{'slabInfo':null,'match':'Match 3','rank':3,'type':'FIXED','prizeAmount':'5.0','betType':'Direct5','pattern':null}]},'resultConfigData':{'type':'RNG','balls':'5','ballsPerCall':5,'interval':0,'duplicateAllowed':false},'donation':[{'image':'https://5.imimg.com/data5/GV/DP/MY-3831378/500ml-plastic-water-bottle-500x500.jpg','title':'WaterBottle'}],'jackpotAmount':5000.0}],'currentDate':'2021-09-23 07:08:17'}}"
        //setParameters(Gson().fromJson(dummyJsonResponse, BoomLottoGameResponse::class.java), false)
        liveDataLoader.postValue(true)
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = BoomLottoRepository().callBoomLottoGameApi(BoomLottoGameRequest())
            withContext(Dispatchers.Main) {
                if (isDrawCompleted)
                    onBoomGameApiDrawCompleteResponse(response)
                else
                    onBoomGameApiResponse(response)
            }
            liveDataLoader.postValue(false)
        }
    }

    private fun onBoomGameApiDrawCompleteResponse(response: Response<BoomLottoGameResponse>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val gameResponse: BoomLottoGameResponse? = response.body()
                    if (gameResponse?.errorCode == 0) {
                        setParameters(gameResponse, true)
                        liveDataBoomGame.postValue(ResponseStatus.Success(gameResponse))
                    } else
                        liveDataBoomGame.postValue(ResponseStatus.Error(gameResponse?.errorMessage ?: "", gameResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveDataBoomGame.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun onBoomGameApiResponse(response: Response<BoomLottoGameResponse>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val gameResponse: BoomLottoGameResponse? = response.body()
                    if (gameResponse?.errorCode == 0) {
                        setParameters(gameResponse, false)
                    } else
                        liveDataBoomGame.postValue(ResponseStatus.Error(gameResponse?.errorMessage ?: "", gameResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveDataBoomGame.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    private fun setParameters(gameResponse: BoomLottoGameResponse, isDrawCompleteEvent: Boolean) {
        val response: List<BoomLottoGameResponse.ResponseData.GameRespVO?>? = gameResponse.responseData?.gameRespVOs

        if (response == null || response.isEmpty()) {
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_001))
        } else {
            val boom: BoomLottoGameResponse.ResponseData.GameRespVO? = response.find { game -> game?.gameCode.equals(BOOM_GAME_CODE, true) }
            if (boom == null) {
                liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_002))
            } else {
                currentTime     = gameResponse.responseData.currentDate ?: ""
                mBoomResponse   = boom

                setPickTypeData(boom)
                setMaxPanelLimit(boom)
                setJackpotAmount(boom)
                setDrawTimer(boom)
                setDonation(boom)
                if (!isDrawCompleteEvent)
                    setGameNumbers(boom)
            }
        }
    }

    private fun setGameNumbers(boomResponse: BoomLottoGameResponse.ResponseData.GameRespVO) {
        val range: List<BoomLottoGameResponse.ResponseData.GameRespVO.NumberConfig.Range?>? = boomResponse.numberConfig?.range
        if (range == null || range.isEmpty()) {
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_003))
        } else {
            range[0]?.ball?.let { ballList ->
                if (ballList.isNotEmpty())
                    liveDataGameNumbersList.postValue(ballList)
                else
                    liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_015))
            } ?: run {
                liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_015))
            }
        }
    }

    private fun setPickTypeData(boomResponse: BoomLottoGameResponse.ResponseData.GameRespVO) {
        val betData = boomResponse.betRespVOs?.find { bet -> bet?.betCode.equals("Direct5", true) }
        if (betData == null) {
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_004))
        } else {
            val price = betData.unitPrice
            if (price != null && price > 0) {
                mUnitPrice                  = betData.unitPrice
                mUnitPriceLiveData.value    = getFormattedAmount(betData.unitPrice.toString().toDouble())
            }
            else
                liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_014))

            val pickData = betData.pickTypeData?.pickType?.find { pick -> pick?.code.equals("Direct5", true) }
            if (pickData == null) {
                liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_005))
            } else {
                setRangeData(pickData)
            }
        }
    }

    private fun setRangeData(pickData: BoomLottoGameResponse.ResponseData.GameRespVO.BetRespVO.PickTypeData.PickType) {
        if (pickData.range == null || pickData.range.isEmpty()) {
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_006))
        } else {
            val range = pickData.range[0]
            setBallSelectionLimit(range)

            mIsQuickPickAllowed.value = range?.qpAllowed.equals("YES")
        }
    }

    private fun setBallSelectionLimit(range: BoomLottoGameResponse.ResponseData.GameRespVO.BetRespVO.PickTypeData.PickType.Range?) {
        range?.pickCount?.let {
            val split: List<String> = it.split(",")
            if (split.size == 2) {
                try {
                    mMaximumSelectionLimitLiveData.value = split[1]
                    mMaximumSelectionLimit = split[1].toInt()
                } catch (e: Exception) {
                    liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_008))
                }
            } else {
                liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_009))
            }
        } ?: run {
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_007))
        }
    }

    private fun setJackpotAmount(boomResponse: BoomLottoGameResponse.ResponseData.GameRespVO) {
        val cost = boomResponse.unitCost?.find { cost -> cost?.currency.equals("AED", true) }
        if (cost != null) {
            val jackpotAmt      = boomResponse.jackpotAmount ?: 0.0
            val price           = cost.price ?: 0.0
            jackpotAmount.value = BuildConfig.CURRENCY_CODE + " " + getFormattedAmount(jackpotAmt * price)
        } else {
            jackpotAmount.value = "---"
        }
    }

    private fun setDrawTimer(boomResponse: BoomLottoGameResponse.ResponseData.GameRespVO) {
        val drawRespVOsList: List<BoomLottoGameResponse.ResponseData.GameRespVO.DrawRespVO?>? = boomResponse.drawRespVOs
        drawRespVOsList?.forEach { it?.isSelected = false }

        if (drawRespVOsList == null || drawRespVOsList.isEmpty() || currentTime.isBlank()) {
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_011))
        } else {
            mDrawList = drawRespVOsList
            val drawRespVO: BoomLottoGameResponse.ResponseData.GameRespVO.DrawRespVO? = drawRespVOsList[0]
            if (drawRespVO?.drawDateTime == null || drawRespVO.drawDateTime.isBlank()) {
                liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_012))
            } else {
                drawRespVO.isSelected = true
                drayDay.value = getAbbreviatedFromDateTime(drawRespVO.drawDateTime)
                startDrawTimer(currentTime, drawRespVO.drawDateTime)
            }
        }
    }

    private fun setMaxPanelLimit(boomResponse: BoomLottoGameResponse.ResponseData.GameRespVO) {
        if (boomResponse.maxPanelAllowed == null || boomResponse.maxPanelAllowed < 1)
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_010))
        else
            mMaxPanelAllowed = boomResponse.maxPanelAllowed
    }

    private fun setDonation(boomResponse: BoomLottoGameResponse.ResponseData.GameRespVO) {
        val donationList = boomResponse.donation
        if (donationList == null || donationList.isEmpty())
            liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_013))
        else {
            val url = donationList[0]?.image
            if (url == null)
                liveDataBoomGame.postValue(ResponseStatus.TechnicalError(R.string.game_not_found_013))
            else {
                imageUrl.value          = url
                donationItemName.value  = donationList[0]?.title
            }
        }
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

            if (millisUntilFinished < 60000 && isHurryUpAllowed) {
                isHurryUpAllowed        = false
                liveDataHurryUp.value   = true
            }
        }

        override fun onFinish() {
            liveDataVibrator.postValue("")
            isHurryUpAllowed = true
            liveDataHurryUp.value = false
            callBoomLottoGameApi(true)
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
        Log.d("log", "Difference: $diff")
        return diff
    }

    private fun startDrawTimer(currentDate: String, futureDate: String) {
        DrawCountDownTimer(getTimeDifference(futureDate, currentDate)).start()
    }

    fun callBuyTicketApi(boomLottoBuyTicketRequest: BoomLottoBuyTicketRequest) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = BoomLottoRepository().callBoomLottoBuyTicketApi(boomLottoBuyTicketRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDataBuyTicket, response) }
            liveDataLoader.postValue(false)
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

    companion object {

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ShapeableImageView, url: String) {
            Glide.with(view.context).load(url).placeholder(R.drawable.icon_place_holder).into(view)
        }
    }

}