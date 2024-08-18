package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.BuildConfig.DOMAIN_NAME
import com.skilrock.boomlotto.utility.PlayerInfo

data class VersionControlRequest(

    @SerializedName("appType")
    @Expose
    val appType: String? = "CASH",

    @SerializedName("currAppVer")
    @Expose
    val currAppVer: String? = BuildConfig.VERSION_NAME,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = DOMAIN_NAME,

    @SerializedName("os")
    @Expose
    val os: String? = "ANDROID",

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken()
)