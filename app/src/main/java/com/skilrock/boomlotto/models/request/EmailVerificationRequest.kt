package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.PlayerInfo

data class EmailVerificationRequest(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("emailId")
    @Expose
    val emailId: String?,

    @SerializedName("merchantPlayerId")
    @Expose
    val merchantPlayerId: String? = PlayerInfo.getPlayerId().toString(),

    @SerializedName("otp")
    @Expose
    val otp: String?
)