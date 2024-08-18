package com.skilrock.boomlotto.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowHomeWinnerListBinding
import com.skilrock.boomlotto.models.response.WinnerListResponse

class HomeWinnerListAdapter(val context: Context, private var winnerList: ArrayList<WinnerListResponse.Data.DGE?>) :
    RecyclerView.Adapter<HomeWinnerListAdapter.HomeWinnerListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeWinnerListViewHolder {
        val binding: RowHomeWinnerListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_home_winner_list, parent, false)
        return HomeWinnerListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeWinnerListViewHolder, position: Int) {
        winnerList[position]?.let { dge ->
            val playerInfoInDge = "xxxx${dge.player?.takeLast(4)} ${context.getString(R.string.won_cap)}"
            holder.rowHomeWinnerListBinding.tvPlayerInfoInDge.text = HtmlCompat.fromHtml(playerInfoInDge, HtmlCompat.FROM_HTML_MODE_LEGACY)
            val winnerCurrencyAndAmountInfoInDge = "<b>${dge.amount}</b>"
            holder.rowHomeWinnerListBinding.tvWinnerCurrencyAndAmountInfoInDge.text = HtmlCompat.fromHtml(winnerCurrencyAndAmountInfoInDge, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    override fun getItemCount(): Int {
        return winnerList.size
    }

    class HomeWinnerListViewHolder(val rowHomeWinnerListBinding: RowHomeWinnerListBinding) :
        RecyclerView.ViewHolder(rowHomeWinnerListBinding.root)
}