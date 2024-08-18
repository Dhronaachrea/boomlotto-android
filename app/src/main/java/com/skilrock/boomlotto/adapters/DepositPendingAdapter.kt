package com.skilrock.boomlotto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowPendingDepositBinding
import com.skilrock.boomlotto.models.response.DepositPendingResponse

class DepositPendingAdapter(private val mList: ArrayList<DepositPendingResponse.Txn?>) :
    RecyclerView.Adapter<DepositPendingAdapter.DepositPendingViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositPendingViewHolder {
        val binding: RowPendingDepositBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_pending_deposit, parent, false)
        return DepositPendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DepositPendingViewHolder, position: Int) {
        holder.binding.model = mList[position]
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class DepositPendingViewHolder(val binding: RowPendingDepositBinding) :
    RecyclerView.ViewHolder(binding.root)

}