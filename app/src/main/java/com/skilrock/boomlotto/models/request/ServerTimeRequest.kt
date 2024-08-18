package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ServerTimeRequest(

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = "Mobile"
)