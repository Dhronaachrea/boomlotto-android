package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.getCurrentDateYMD
import com.skilrock.boomlotto.utility.getPreviousDateYMD


data class DepositPendingRequest(

        @SerializedName("domainName")
        @Expose
        val domainName: String? = BuildConfig.DOMAIN_NAME,

        @SerializedName("fromDate")
        @Expose
        val fromDate: String? = getPreviousDateYMD(180),

        @SerializedName("toDate")
        @Expose
        val toDate: String? = getCurrentDateYMD(),

        @SerializedName("limit")
        @Expose
        val limit: String? = "100",

        @SerializedName("offset")
        @Expose
        val offset: String? = "1",

        @SerializedName("playerId")
        @Expose
        val playerId: Int? = PlayerInfo.getPlayerId(),

        @SerializedName("playerToken")
        @Expose
        val playerToken: String? = PlayerInfo.getPlayerToken(),

        @SerializedName("txnType")
        @Expose
        val txnType: String?

)