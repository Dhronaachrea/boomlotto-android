package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowWinnerListIgeBinding
import com.skilrock.boomlotto.models.response.WinnerListResponse
import java.text.SimpleDateFormat

class WinnerListIgeAdapter(val context: Context, private val winnerList: ArrayList<WinnerListResponse.Data.IGE?>) :
    RecyclerView.Adapter<WinnerListIgeAdapter.WinnerListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinnerListViewHolder {
        val binding: RowWinnerListIgeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_winner_list_ige, parent, false)
        return WinnerListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WinnerListViewHolder, position: Int) {
        winnerList[position]?.let { ige ->
            val playerInfoInIge = "XXXX${ige.player?.takeLast(4)} ${context.getString(R.string.won_cap)}"
            holder.rowWinnerListBinding.tvPlayerInfoInIge.text = HtmlCompat.fromHtml(playerInfoInIge, HtmlCompat.FROM_HTML_MODE_LEGACY)

            val winnerCurrencyAndAmountInfoInIge = "<b>${ige.amount}</b>"
            holder.rowWinnerListBinding.tvWinnerCurrencyAndAmountInIge.text = HtmlCompat.fromHtml(winnerCurrencyAndAmountInfoInIge, HtmlCompat.FROM_HTML_MODE_LEGACY)

            holder.rowWinnerListBinding.tvGameWinNameInIge.text = ige.gameName

            if (ige.txnDate != null)
                holder.rowWinnerListBinding.tvDateAnTimeInIge.text = getNewAbbreviatedFromDateTime(ige.txnDate)
            else
                holder.rowWinnerListBinding.tvDateAnTimeInIge.visibility = View.GONE
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
        val output  = SimpleDateFormat("MMM dd, yyyy, hh:mm a")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return null
    }

    class WinnerListViewHolder(val rowWinnerListBinding: RowWinnerListIgeBinding) :
        RecyclerView.ViewHolder(rowWinnerListBinding.root)

}
