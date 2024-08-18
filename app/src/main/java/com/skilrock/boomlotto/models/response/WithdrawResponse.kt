package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WithdrawResponse(

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMsg")
    @Expose
    override val errorMessage: String?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("txnId")
    @Expose
    val txnId: String?,

    @SerializedName("txnDate")
    @Expose
    val txnDate: String?,

    @SerializedName("regTxnNo")
    @Expose
    val regTxnNo: String?,

    @SerializedName("userTxnId")
    @Expose
    val userTxnId: String?,

    @SerializedName("isOtpEnabled")
    @Expose
    val isOtpEnabled: String?
) : AppResponse