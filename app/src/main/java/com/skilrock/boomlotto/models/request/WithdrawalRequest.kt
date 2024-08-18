package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.DEVICE_TYPE
import com.skilrock.boomlotto.utility.PlayerInfo

data class WithdrawalRequest(

    @SerializedName("amount")
    @Expose
    val amount: String?,

    @SerializedName("currencyCode")
    @Expose
    val currencyCode: String? = PlayerInfo.getCurrency(),

    @SerializedName("paymentAccId")
    @Expose
    val paymentAccId: Int?,

    @SerializedName("paymentTypeCode")
    @Expose
    val paymentTypeCode: String?,

    @SerializedName("paymentTypeId")
    @Expose
    val paymentTypeId: Int?,

    @SerializedName("subTypeId")
    @Expose
    val subTypeId: Int?,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = DEVICE_TYPE,

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("txnType")
    @Expose
    val txnType: String? = "WITHDRAWAL",

    @SerializedName("userAgent")
    @Expose
    val userAgent: String? = System.getProperty("http.agent")
)