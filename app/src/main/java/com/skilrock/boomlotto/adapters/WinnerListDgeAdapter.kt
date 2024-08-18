package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowWinnerListDgeBinding
import com.skilrock.boomlotto.models.response.WinnerListResponse
import java.text.SimpleDateFormat

class WinnerListDgeAdapter(val context: Context, private val winnerList: ArrayList<WinnerListResponse.Data.DGE?>) :
    RecyclerView.Adapter<WinnerListDgeAdapter.WinnerListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinnerListViewHolder {
        val binding: RowWinnerListDgeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_winner_list_dge, parent, false)
        return WinnerListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WinnerListViewHolder, position: Int) {
        winnerList[position]?.let { dge ->
            val playerInfoInDge = "XXXX${dge.player?.takeLast(4)} ${context.getString(R.string.won_cap)}"
            holder.rowWinnerListBinding.tvplayerInfoInDge.text = HtmlCompat.fromHtml(playerInfoInDge, HtmlCompat.FROM_HTML_MODE_LEGACY)

            val winnerCurrencyAndAmountInfoInDge = "<b>${dge.amount}</b>"
            holder.rowWinnerListBinding.tvWinnerCurrencyAndAmountInfoInDge  .text = HtmlCompat.fromHtml(winnerCurrencyAndAmountInfoInDge, HtmlCompat.FROM_HTML_MODE_LEGACY)

            holder.rowWinnerListBinding.tvGameWinNameInDge.text = dge.gameName

            if (dge.txnDate != null)
                holder.rowWinnerListBinding.tvDateAnTimeInDge.text = getNewAbbreviatedFromDateTime(dge.txnDate)
            else
                holder.rowWinnerListBinding.tvDateAnTimeInDge.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return winnerList.size
    }

    /*@SuppressLint("SimpleDateFormat")
    fun getAbbreviatedFromDateTime(dateTime: String): String? {
        val input   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output  = SimpleDateFormat("MMM dd, EEEE, hh:mm a")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return null
    }*/

    @SuppressLint("SimpleDateFormat")
    fun getNewAbbreviatedFromDateTime(dateTime: String): String? {
        val input   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output  = SimpleDateFormat("MMM dd, yyyy, hh:mm aa")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            return dateTime
        }
        return dateTime
    }

    class WinnerListViewHolder(val rowWinnerListBinding: RowWinnerListDgeBinding) :
        RecyclerView.ViewHolder(rowWinnerListBinding.root)

}
