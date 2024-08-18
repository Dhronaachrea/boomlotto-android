package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MyTransactionsResponse(

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("openingBalanceDate")
    @Expose
    val openingBalanceDate: String?,

    @SerializedName("respMsg")
    @Expose
    override val errorMessage: String?,

    @SerializedName("txnList")
    @Expose
    val txnList: ArrayList<Txn?>?
) : AppResponse {
    data class Txn(

        @SerializedName("balance")
        @Expose
        val balance: Double?,

        @SerializedName("creditAmount")
        @Expose
        val creditAmount: Double?,

        @SerializedName("currency")
        @Expose
        val currency: String?,

        @SerializedName("currencyId")
        @Expose
        val currencyId: Int?,

        @SerializedName("debitAmount")
        @Expose
        val debitAmount: Double?,

        @SerializedName("gameGroup")
        @Expose
        val gameGroup: String?,

        @SerializedName("openingBalance")
        @Expose
        val openingBalance: Double?,

        @SerializedName("particular")
        @Expose
        val particular: String?,

        @SerializedName("subwalletTxn")
        @Expose
        val subwalletTxn: String?,

        @SerializedName("transactionDate")
        @Expose
        val transactionDate: String?,

        @SerializedName("transactionId")
        @Expose
        val transactionId: Int?,

        @SerializedName("txnAmount")
        @Expose
        val txnAmount: Double?,

        @SerializedName("txnType")
        @Expose
        val txnType: String?,

        @SerializedName("withdrawableBalance")
        @Expose
        val withdrawableBalance: Double?
    )
}