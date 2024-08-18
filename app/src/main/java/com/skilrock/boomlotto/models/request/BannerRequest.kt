package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BannerRequest(

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = "MOBILE"
)