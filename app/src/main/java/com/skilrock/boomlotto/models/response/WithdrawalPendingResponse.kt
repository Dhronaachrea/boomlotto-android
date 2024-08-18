package com.skilrock.boomlotto.models.response


import android.annotation.SuppressLint
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.utility.PlayerInfo
import java.text.SimpleDateFormat


data class WithdrawalPendingResponse(

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMsg")
    @Expose
    override val errorMessage: String?,

    @SerializedName("txnList")
    @Expose
    val txnList: ArrayList<Txn?>?
) : AppResponse {
    data class Txn(

        @SerializedName("amount")
        @Expose
        val amount: Double?,

        @SerializedName("status")
        @Expose
        val status: String?,

        @SerializedName("txnDate")
        @Expose
        val txnDate: String?,

        @SerializedName("userTxnId")
        @Expose
        val userTxnId: Int?,

        @SerializedName("transactionId")
        @Expose
        val transactionId: String?,

        @SerializedName("txnType")
        @Expose
        val txnType: String?,

        @SerializedName("verficationCode")
        @Expose
        val verficationCode: Any?
    ) {

        @SuppressLint("SimpleDateFormat")
        fun getFormattedDate() : String {
            return try {
                val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                val formatter   = SimpleDateFormat("MMM dd yyyy, hh:mm aa")
                parser.parse(txnDate)?.let { formatter.format(it) } ?: ""
            } catch (e: Exception) {
                ""
            }
        }

        fun getFormattedAmount() : String {
            return PlayerInfo.getCurrencyDisplayCode() + " " + amount
        }

    }
}