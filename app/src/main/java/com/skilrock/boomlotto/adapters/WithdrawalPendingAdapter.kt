package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowPendingWithdrawalBinding
import com.skilrock.boomlotto.models.response.WithdrawalPendingResponse
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.getFormattedAmount
import java.text.SimpleDateFormat

class WithdrawalPendingAdapter(val context: Context, private val mList: ArrayList<WithdrawalPendingResponse.Txn?>) :
    RecyclerView.Adapter<WithdrawalPendingAdapter.WithdrawalPendingViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawalPendingViewHolder {
        val binding: RowPendingWithdrawalBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_pending_withdrawal, parent, false)
        return WithdrawalPendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WithdrawalPendingViewHolder, position: Int) {
        val row = holder.binding
        mList[position]?.let {
            row.tvTxnId.text = it.transactionId
            row.tvTxnDate.text = getFormattedDate(it.txnDate.toString())
            row.tvStatus.text =it.status
            val totalAmount = "${getCountryCode()} ${getFormattedAmount(it.amount.toString().toDouble())}"
            row.tvAmount.text = totalAmount
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(transactionDate: String) : String {
        return try {
            //2021-08-31 12:15:08
            val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd yyyy, hh:mm aa")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: context.getString(R.string.na)
        } catch (e: Exception) {
            context.getString(R.string.na)
        }
    }

    private fun getCountryCode() : String {
        return PlayerInfo.getCurrencyDisplayCode()
    }

    class WithdrawalPendingViewHolder(val binding: RowPendingWithdrawalBinding) :
    RecyclerView.ViewHolder(binding.root)

}