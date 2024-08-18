package com.skilrock.boomlotto.models.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddNewAccountResponse(

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMsg")
    @Expose
    override val errorMessage: String?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("verificationCode")
    @Expose
    val verificationCode: String?,

    var isWithdrawalApiCallRequired: Boolean? = false
) : AppResponse
