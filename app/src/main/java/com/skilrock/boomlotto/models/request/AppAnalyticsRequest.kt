package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.utility.LOGIN_DEVICE

data class AppAnalyticsRequest(

    @SerializedName("analyticsData")
    @Expose
    val analyticsData: Any?,

    @SerializedName("device")
    @Expose
    val device: String = LOGIN_DEVICE
)