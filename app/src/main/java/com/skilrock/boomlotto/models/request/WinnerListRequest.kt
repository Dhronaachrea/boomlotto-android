package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WinnerListRequest(

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = "Mobile",

    @SerializedName("serviceCode")
    @Expose
    val serviceCode: List<String> = listOf("DGE","IGE")
)