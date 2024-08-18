package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.PlayerInfo

data class BonusRequest(

    @SerializedName("aliasName")
    @Expose
    val aliasName: String = BuildConfig.DOMAIN_NAME,

    @SerializedName("txnId")
    @Expose
    val txnId: Long = PlayerInfo.getPlayerId().toLong(),

    @SerializedName("userId")
    @Expose
    val userId: Int = PlayerInfo.getPlayerId()
)