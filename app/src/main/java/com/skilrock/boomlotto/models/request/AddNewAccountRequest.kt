package com.skilrock.boomlotto.models.request


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.DEVICE_TYPE
import com.skilrock.boomlotto.utility.PlayerInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddNewAccountRequest(

    @SerializedName("accHolderName")
    @Expose
    val accHolderName: String?,

    @SerializedName("accNum")
    @Expose
    val accNum: String,

    @SerializedName("accType")
    @Expose
    val accType: String? = "SAVING",

    @SerializedName("currencyCode")
    @Expose
    val currencyCode: String?,

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = DEVICE_TYPE,

    @SerializedName("documentName")
    @Expose
    val documentName: String? = "BANK_STATEMENT",

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("ifscCode")
    @Expose
    val ifscCode: String?,

    @SerializedName("merchantCode")
    @Expose
    val merchantCode: String? = "",

    @SerializedName("nickName")
    @Expose
    val nickName: String?,

    @SerializedName("paymentTypeCode")
    @Expose
    val paymentTypeCode: String?,

    @SerializedName("paymentTypeId")
    @Expose
    val paymentTypeId: String?,

    @SerializedName("playerId")
    @Expose
    val playerId: String? = PlayerInfo.getPlayerId().toString(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("subTypeId")
    @Expose
    val subTypeId: String?,

    @SerializedName("verifyOtp")
    @Expose
    var verifyOtp: String? = null
) : Parcelable