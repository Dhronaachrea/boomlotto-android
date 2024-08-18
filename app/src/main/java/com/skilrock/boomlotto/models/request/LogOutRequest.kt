package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.PlayerInfo

data class LogOutRequest(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken()
)