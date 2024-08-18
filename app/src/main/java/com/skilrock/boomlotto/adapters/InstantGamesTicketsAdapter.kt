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
import com.skilrock.boomlotto.databinding.RowTicketsIgeBinding
import com.skilrock.boomlotto.models.response.InstantGameTicketListResponse
import com.skilrock.boomlotto.utility.TIME_ZONE
import com.skilrock.boomlotto.utility.getFormattedAmount
import java.text.SimpleDateFormat

class InstantGamesTicketsAdapter(val context: Context) :
    RecyclerView.Adapter<InstantGamesTicketsAdapter.InstantGamesTicketsViewHolder>() {

    private var ticketList: ArrayList<InstantGameTicketListResponse.ResponseData.Ticket?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstantGamesTicketsViewHolder {
        val binding: RowTicketsIgeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_tickets_ige, parent, false)
        return InstantGamesTicketsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstantGamesTicketsViewHolder, position: Int) {
        val row = holder.rowTicketsIgeBinding
        ticketList?.get(position)?.let { ticket ->
            row.tvLine1.text    = ticket.gameDisplayName
            row.tvLine2.text    = getDisplayTransactionDate(ticket.transactionDate)
            row.tvLine4.text    = ( "#" + ticket.ticketNumber)
            row.tvCurrency.text = ticket.ticketDetails?.txnCurrency ?: context.getString(R.string.na)
            row.tvAmount.text   = getFormattedAmount(ticket.ticketDetails?.saleAmount.toString().toDouble())

            ticket.ticketDetails?.winstatus?.let { status ->
                when (status) {
                    "WIN", "CLAIMED" -> {
                        val currency    = ticket.ticketDetails.txnCurrency ?: ""
                        val amount      = getFormattedAmount(ticket.ticketDetails.winningAmount.toString().toDouble())
                        val text        = ("$currency <b>$amount</b>")
                        row.tvCurrencyAndAmount.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        row.llWonTicketLabel.visibility = View.VISIBLE
                    }
                    else -> {
                        row.llWonTicketLabel.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return ticketList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun putTicketList(list: ArrayList<InstantGameTicketListResponse.ResponseData.Ticket?>) {
        ticketList = list
        notifyDataSetChanged()
    }

    private fun getDisplayTransactionDate(strDate: String?) : String {
        return if (strDate == null)
            context.getString(R.string.na)
        else
            getFormattedDate(strDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(transactionDate: String) : String {
        return try {
            //2021-08-31 12:15:08
            val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd yyyy, hh:mm aa")
            parser.parse(transactionDate)?.let { (formatter.format(it) + TIME_ZONE) } ?: transactionDate
        } catch (e: Exception) {
            transactionDate
        }
    }

    class InstantGamesTicketsViewHolder(val rowTicketsIgeBinding: RowTicketsIgeBinding) :
        RecyclerView.ViewHolder(rowTicketsIgeBinding.root)
}