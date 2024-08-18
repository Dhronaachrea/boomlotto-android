package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.BOOM_GAME_CODE
import com.skilrock.boomlotto.utility.PlayerInfo

data class BoomLottoGameRequest(

    @SerializedName("domainCode")
    @Expose
    val domainCode: String = BuildConfig.DOMAIN_NAME,

    @SerializedName("field")
    @Expose
    val `field`: List<String?> = listOf("DRAW_INFO", "CURRENCY", "BET_INFO", "DRAW_PRIZE_MULTIPLIER", "WINNING_RESULT", "NUMBER_CONFIG", "GAMES_SCHEMA"),

    @SerializedName("gameCodes")
    @Expose
    val gameCodes: List<String?> = listOf(BOOM_GAME_CODE),

    @SerializedName("lastTicketNumber")
    @Expose
    val lastTicketNumber: String? = "",

    @SerializedName("numberOfDraw")
    @Expose
    val numberOfDraw: Int? = 0,

    @SerializedName("playerCurrencyCode")
    @Expose
    val playerCurrencyCode: String? = BuildConfig.CURRENCY_CODE,

    @SerializedName("sessionId")
    @Expose
    val sessionId: String? = PlayerInfo.getPlayerToken()
)