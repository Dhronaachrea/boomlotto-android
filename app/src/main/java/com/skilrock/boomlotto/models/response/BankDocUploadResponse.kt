package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BankDocUploadResponse(

    @SerializedName("data")
    @Expose
    val `data`: String?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?,

    var isWithdrawalApiCallRequired: Boolean = false
) : AppResponse