package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowBoomIncompleteLineBinding

class BoomIncompleteLineAdapter :
    RecyclerView.Adapter<BoomIncompleteLineAdapter.IncompleteLIneViewHolder>() {

    private var ballList: ArrayList<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncompleteLIneViewHolder {
        val binding: RowBoomIncompleteLineBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_boom_incomplete_line, parent, false)
        return IncompleteLIneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncompleteLIneViewHolder, position: Int) {
        val ball = ballList?.get(position)
        ball?.let {
            holder.rowBoomIncompleteLineBinding.tvBall.text = it
        }
    }

    override fun getItemCount(): Int {
        return ballList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBallsList(list: ArrayList<String>) {
        ballList = list
        notifyDataSetChanged()
    }

    class IncompleteLIneViewHolder(val rowBoomIncompleteLineBinding: RowBoomIncompleteLineBinding) :
        RecyclerView.ViewHolder(rowBoomIncompleteLineBinding.root)

}