package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.PlayerInfo

data class EmailOtpRequest(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("emailId")
    @Expose
    val emailId: String?,

    @SerializedName("playerId")
    @Expose
    val playerId: String? = PlayerInfo.getPlayerId().toString(),

    @SerializedName("isOtpVerification")
    @Expose
    val isOtpVerification: String = "YES"
)