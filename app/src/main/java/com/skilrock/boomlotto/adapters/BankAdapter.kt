package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowBankBinding
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse

class BankAdapter(private val onBankSelected:(PaymentOptionsResponse.PayTypeMap)->Unit) : RecyclerView.Adapter<BankAdapter.BankViewHolder>() {

    private var mList: List<PaymentOptionsResponse.PayTypeMap>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val binding: RowBankBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_bank, parent, false)
        return BankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        holder.rowBankBinding.tvBank.text = ("• " + mList?.get(position)?.subTypeValue)
        holder.rowBankBinding.llRow.setOnClickListener {
            mList?.let { list -> onBankSelected(list[position]) }
        }
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBankList(list: List<PaymentOptionsResponse.PayTypeMap>?) {
        mList = list
        notifyDataSetChanged()
    }

    class BankViewHolder(val rowBankBinding: RowBankBinding) :
        RecyclerView.ViewHolder(rowBankBinding.root)

}