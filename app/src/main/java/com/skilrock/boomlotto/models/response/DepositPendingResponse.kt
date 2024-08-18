package com.skilrock.boomlotto.models.response


import android.annotation.SuppressLint
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.utility.PlayerInfo
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


data class DepositPendingResponse(

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
            return PlayerInfo.getCurrencyDisplayCode() + " " + getFormattedAmountWinComma(amount ?: 0.0)
        }

        private fun getFormattedAmountWinComma(amount: Double): String {
            var formattedAmount = amount.toString()
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("AED")

            format.currency?.let{
                val amt = format.format(amount)
                formattedAmount = amt.replace(it.symbol, "")
            }
            return formattedAmount
        }

    }
}