package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.BOOM_GAME_CODE

data class ResultRequest(

    @SerializedName("domainCode")
    @Expose
    val domainCode: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("fromDate")
    @Expose
    val fromDate: String?,

    @SerializedName("gameCode")
    @Expose
    val gameCode: String? = BOOM_GAME_CODE,

    @SerializedName("merchantCode")
    @Expose
    val merchantCode: String? = "Weaver",

    @SerializedName("orderByOperator")
    @Expose
    val orderByOperator: String? = "DESC",

    @SerializedName("orderByType")
    @Expose
    val orderByType: String? = "draw_datetime",

    @SerializedName("page")
    @Expose
    val page: Int? = 1,

    @SerializedName("size")
    @Expose
    val size: Int? = 1000,

    @SerializedName("toDate")
    @Expose
    val toDate: String?
)