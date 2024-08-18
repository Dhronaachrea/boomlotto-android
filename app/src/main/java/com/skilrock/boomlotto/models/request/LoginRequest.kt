package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.DEVICE_TYPE
import com.skilrock.boomlotto.utility.LOGIN_DEVICE
import com.skilrock.boomlotto.utility.REQUEST_IP

data class LoginRequest(

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = DEVICE_TYPE,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("loginDevice")
    @Expose
    val loginDevice: String? = LOGIN_DEVICE,

    @SerializedName("referCode")
    @Expose
    var referCode: String? = null,

    @SerializedName("referSource")
    @Expose
    var referSource: String? = null,

    @SerializedName("currencyCode")
    @Expose
    var currencyCode: String? = BuildConfig.CURRENCY_CODE,

    @SerializedName("mobileNo")
    @Expose
    val mobileNo: String?,

    @SerializedName("countryCode")
    @Expose
    val countryCode: String?,

    @SerializedName("otp")
    @Expose
    val otp: String?,

    @SerializedName("requestIp")
    @Expose
    val requestIp: String? = REQUEST_IP,

    @SerializedName("userAgent")
    @Expose
    val userAgent: String? = System.getProperty("http.agent")
)