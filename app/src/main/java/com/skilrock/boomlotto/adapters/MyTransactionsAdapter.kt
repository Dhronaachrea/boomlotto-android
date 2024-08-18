package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowMyTransactionsBinding
import com.skilrock.boomlotto.models.response.MyTransactionsResponse.Txn
import com.skilrock.boomlotto.utility.TIME_ZONE
import com.skilrock.boomlotto.utility.getFormattedAmount
import java.text.SimpleDateFormat

class MyTransactionsAdapter(val context: Context) :
    RecyclerView.Adapter<MyTransactionsAdapter.MyTransactionsViewHolder>() {

    private var transactionList: ArrayList<Txn?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTransactionsViewHolder {
        val binding: RowMyTransactionsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_my_transactions, parent, false)
        return MyTransactionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyTransactionsViewHolder, position: Int) {
        val row = holder.binding
        transactionList?.get(position)?.let { txn: Txn ->
            txn.transactionDate?.let {
                val dateAndTime = "${getDateToShow(it)}${context.getString(R.string.comma)} ${getTime(it)}"
                row.tvDateAndTime.text = dateAndTime
            }

            if (txn.txnType == "PLR_WINNING") {
                row.llTransactionDetailsCard.setBackgroundResource(R.drawable.card_border_colored)
                //row.tvLine1.setTextColor(Color.parseColor("#02d1a0"))
                row.tvLine1.setTextColor(Color.parseColor("#34DAB3"))
            } else {
                row.llTransactionDetailsCard.setBackgroundResource(R.drawable.card_border)
                row.tvLine1.setTextColor(Color.parseColor("#00004C"))
            }

            row.tvLine1.text = getTxnTypeCaption(getHeadingParticularStr(txn))

            val creditPresent = getTxnId(txn)
            if (creditPresent.contains("Credit Amount:")) {
                row.ivCreditPlusDebitMinus.setBackgroundResource(R.drawable.outline_add_24)
            } else {
                row.ivCreditPlusDebitMinus.setBackgroundResource(R.drawable.ic_icon_minus)
            }

            row.tvLine2.text = getOnlyTxnId(txn)
            val x = getCrDrAmt(txn)
            val y = x.split(" ")
            row.tvCurrency.text = y[2]
            row.tvCreditDebitAmount.text = getFormattedAmount(y[3].toString().toDouble())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTransactionsList(list: ArrayList<Txn?>) {
        transactionList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return transactionList?.size ?: 0
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateToShow(transactionDate: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val formatter = SimpleDateFormat("MMM d, yyyy")
            parser.parse(transactionDate)?.let { formatter.format(it) }
                ?: context.getString(R.string.na)
        } catch (e: Exception) {
            context.getString(R.string.na)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(transactionDate: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val formatter = SimpleDateFormat("hh:mm aa")
            parser.parse(transactionDate)?.let { (formatter.format(it) + TIME_ZONE) } ?: transactionDate
        } catch (e: Exception) {
            transactionDate
        }
    }

    private fun getTxnTypeCaption(txnType: String): String {
        return when (txnType) {
            "PLR_WAGER"                     -> context.getString(R.string.wager)
            "PLR_DEPOSIT"                   -> context.getString(R.string.deposit)
            "PLR_WITHDRAWAL"                -> context.getString(R.string.withdrawal)
            "PLR_WINNING"                   -> context.getString(R.string.winning)
            "PLR_WAGER_REFUND"              -> context.getString(R.string.wager_refund)
            "TRANSFER_IN"                   -> context.getString(R.string.cash_in)
            "TRANSFER_OUT"                  -> context.getString(R.string.cash_out)
            "TRANSFER_OUT_REFUND"           -> context.getString(R.string.refund)
            "PLR_DEPOSIT_AGAINST_CANCEL"    -> context.getString(R.string.withdrawal_cancel)
            "BO_CORRECTION"                 -> context.getString(R.string.payment_correction)
            else -> txnType
        }
    }

    private fun getTxnId(txn: Txn): String {
        return "${context.getString(R.string.txn_id)} ${txn.transactionId} ${getCrDrAmt(txn)}"
    }

    private fun getOnlyTxnId(txn: Txn): String {
        return "${context.getString(R.string.txn_id)} ${txn.transactionId}"
    }

    private fun getCrDrAmt(txn: Txn): String {
        if (txn.creditAmount == null && txn.debitAmount != null)
            return "\n${context.getString(R.string.debit_amount)} ${(txn.currency ?: BuildConfig.CURRENCY_CODE)} ${String.format("%.2f", txn.debitAmount)}"
        if (txn.debitAmount == null && txn.creditAmount != null)
            return "\n${context.getString(R.string.credit_amount)} ${(txn.currency ?: BuildConfig.CURRENCY_CODE)} ${String.format("%.2f", txn.creditAmount)}"
        return ""
    }

    private fun getHeadingParticularStr(txn: Txn): String {
        return txn.txnType ?: ""
    }

    class MyTransactionsViewHolder(val binding: RowMyTransactionsBinding) :
        RecyclerView.ViewHolder(binding.root)
}