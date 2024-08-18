package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowBoomBallPanelBinding

class BoomPanelBallAdapter(private val mMaxSelectionCount: Int) : RecyclerView.Adapter<BoomPanelBallAdapter.PanelBallViewHolder>() {

    private var mList: ArrayList<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanelBallViewHolder {
        val binding: RowBoomBallPanelBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_boom_ball_panel, parent, false)
        return PanelBallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PanelBallViewHolder, position: Int) {
        try {
            holder.rowBoomBallPanelBinding.tvBall.text = mList?.get(position) ?: ""
            holder.rowBoomBallPanelBinding.tvBall.setTextColor(Color.parseColor("#444444"))
        } catch (e: Exception) {
            holder.rowBoomBallPanelBinding.tvBall.text = "?"
            holder.rowBoomBallPanelBinding.tvBall.setTextColor(Color.parseColor("#bcd6ec"))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBallList(list: ArrayList<String>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mMaxSelectionCount
    }

    class PanelBallViewHolder(val rowBoomBallPanelBinding: RowBoomBallPanelBinding) :
        RecyclerView.ViewHolder(rowBoomBallPanelBinding.root)

}