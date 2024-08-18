package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.DEVICE_TYPE
import com.skilrock.boomlotto.utility.PlayerInfo

data class PaymentOptionsRequest(

        @SerializedName("currencyCode")
        @Expose
        val currencyCode: String? = PlayerInfo.getCurrency(),

        @SerializedName("deviceType")
        @Expose
        val deviceType: String? = DEVICE_TYPE,

        @SerializedName("domainName")
        @Expose
        val domainName: String? = BuildConfig.DOMAIN_NAME,

        @SerializedName("playerId")
        @Expose
        val playerId: Int? = PlayerInfo.getPlayerId(),

        @SerializedName("playerToken")
        @Expose
        val playerToken: String? = PlayerInfo.getPlayerToken(),

        @SerializedName("txnType")
        @Expose
        val txnType: String,

        @SerializedName("userAgent")
        @Expose
        val userAgent: String? = System.getProperty("http.agent")
)