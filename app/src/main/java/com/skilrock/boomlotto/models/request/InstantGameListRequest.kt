package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig.CURRENCY_CODE
import com.skilrock.boomlotto.utility.SERVICE

data class InstantGameListRequest(

    @SerializedName("currencyCode")
    @Expose
    val currencyCode: String? = CURRENCY_CODE,

    @SerializedName("isMobile")
    @Expose
    val isMobile: Boolean? = true,

    @SerializedName("lobbyCode")
    @Expose
    val lobbyCode: String? = "IGE",

    @SerializedName("service")
    @Expose
    val service: String? = SERVICE
)