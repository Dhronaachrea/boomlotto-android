package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.PlayerInfo

data class PlayerInboxDeleteRequest(

    @SerializedName("activity")
    @Expose
    val activity: String? = "DELETE",

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("inboxIdList")
    @Expose
    val inboxIdList: ArrayList<Int>,

    @SerializedName("limit")
    @Expose
    val limit: Int? = 500,

    @SerializedName("offset")
    @Expose
    val offset: Int? = 0,

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken()
)