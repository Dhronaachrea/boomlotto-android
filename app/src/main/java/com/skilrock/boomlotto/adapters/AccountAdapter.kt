package com.skilrock.boomlotto.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowAccountBinding
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse

class AccountAdapter(private val context: Context, private val mListAccount: ArrayList<Pair<PaymentOptionsResponse.PayTypeMap.AccountDetail, PaymentOptionsResponse.PayTypeMap>>,
                     private val onBankSelected:(PaymentOptionsResponse.PayTypeMap, Int)->Unit) : RecyclerView.Adapter<AccountAdapter.BankViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val binding: RowAccountBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_account, parent, false)
        return BankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        val row     = holder.rowAccountBinding
        val pair    = mListAccount[position]

        row.tvBank.text             = pair.second.subTypeValue
        row.tvName.text             = pair.first.accountHolderName
        row.tvAccountNumber.text    = pair.first.accNum

        val status                  = pair.first.verificationStatus
        if (status.isNotBlank()) {
            row.tvStatus.visibility = View.VISIBLE
            row.tvStatus.text       = (status.lowercase().take(1).uppercase() + status.lowercase().takeLast(status.length - 1))
        } else
            row.tvStatus.visibility = View.GONE

        if (pair.first.isSelected) {
            row.llRow.strokeColor = ContextCompat.getColor(context, R.color.color_app_green)
        } else {
            row.llRow.strokeColor = ContextCompat.getColor(context, R.color.card_stroke_color)
        }

        row.llRow.setOnClickListener {
            onBankSelected(pair.second, pair.first.paymentAccId)
            mListAccount.forEachIndexed { index, pairAccount ->
                pairAccount.first.isSelected = false
                notifyItemChanged(index, mListAccount[position])
            }
            pair.first.isSelected = true
            notifyItemChanged(position, mListAccount[position])
        }
    }

    override fun getItemCount(): Int {
        return mListAccount.size
    }

    class BankViewHolder(val rowAccountBinding: RowAccountBinding) :
        RecyclerView.ViewHolder(rowAccountBinding.root)

}