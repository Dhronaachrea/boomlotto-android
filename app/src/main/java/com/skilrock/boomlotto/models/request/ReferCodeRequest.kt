package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig.DOMAIN_NAME

data class ReferCodeRequest(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = DOMAIN_NAME,

    @SerializedName("referCode")
    @Expose
    val referCode: String?
)