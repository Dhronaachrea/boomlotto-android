package com.skilrock.boomlotto.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowResultMatchInfoBinding
import com.skilrock.boomlotto.models.response.ResultResponse

class ResultMatchAdapter(val context: Context, private val matchList: ArrayList<ResultResponse.ResponseData.ResultData.ResultInfo.MatchInfo?>) :
    RecyclerView.Adapter<ResultMatchAdapter.ResultMatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultMatchViewHolder {
        val binding: RowResultMatchInfoBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_result_match_info, parent, false)
        return ResultMatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultMatchViewHolder, position: Int) {
        val row = holder.rowResultMatchInfoBinding
        matchList[position]?.let { matchInfo ->
            if (matchInfo.rowIndex % 2 == 0)
                row.llContainer.setBackgroundColor(Color.parseColor("#f0f0f4"))
            else
                row.llContainer.setBackgroundColor(Color.parseColor("#FFFFFF"))

            row.tvMatch.text        = matchInfo.match ?: "-"
            row.tvWinner.text       = matchInfo.noOfWinners ?: "-"
            row.tvPrizePool.text    = "-"
            row.tvWinning.text      = matchInfo.amount ?: "-"
        }
    }

    override fun getItemCount(): Int {
        return matchList.size
    }

    class ResultMatchViewHolder(val rowResultMatchInfoBinding: RowResultMatchInfoBinding) : RecyclerView.ViewHolder(rowResultMatchInfoBinding.root)
}