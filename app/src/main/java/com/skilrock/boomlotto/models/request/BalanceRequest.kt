package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.PlayerInfo

data class BalanceRequest(

        @SerializedName("domainName")
        @Expose
        val domainName: String? = BuildConfig.DOMAIN_NAME,

        @SerializedName("playerId")
        @Expose
        val playerId: String? = PlayerInfo.getPlayerId().toString(),

        @SerializedName("playerToken")
        @Expose
        val playerToken: String? = PlayerInfo.getPlayerToken(),

        @SerializedName("refill")
        @Expose
        val refill: Boolean? = false,

        @SerializedName("ussd")
        @Expose
        val ussd: Boolean? = false,

        @SerializedName("walletType")
        @Expose
        val walletType: String? = "CASH"
)