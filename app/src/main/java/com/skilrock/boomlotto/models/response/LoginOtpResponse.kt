package com.skilrock.boomlotto.models.response


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginOtpResponse(

    @SerializedName("data")
    @Expose
    val data: Data?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?
) : AppResponse, Parcelable  {

    @Parcelize
    data class Data(

        @SerializedName("domainId")
        @Expose
        val domainId: Int?,

        @SerializedName("emailVerificationCode")
        @Expose
        val emailVerificationCode: String?,

        @SerializedName("emailVerificationExpiry")
        @Expose
        val emailVerificationExpiry: Long?,

        @SerializedName("id")
        @Expose
        val id: Int?,

        @SerializedName("mobVerificationCode")
        @Expose
        val mobVerificationCode: String?,

        @SerializedName("mobVerificationExpiry")
        @Expose
        val mobVerificationExpiry: Long?,

        @SerializedName("mobileNo")
        @Expose
        val mobileNo: String?,

        @SerializedName("otpActionType")
        @Expose
        val otpActionType: String?,

        var mobileNumberWithoutCountryCode: String = "",

        var isdCode: String = "",

        var countryCode: String = ""
    ) : Parcelable
}