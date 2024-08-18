package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowTicketsDgeBinding
import com.skilrock.boomlotto.models.response.MyTicketsListResponse
import com.skilrock.boomlotto.utility.TIME_ZONE
import com.skilrock.boomlotto.utility.getFormattedAmount
import java.text.SimpleDateFormat

class DgeTicketsAdapter(val context: Context, val onTicketClick:(MyTicketsListResponse.ResponseData.Ticket)->Unit) :
    RecyclerView.Adapter<DgeTicketsAdapter.DgeTicketsViewHolder>() {

    private var lastClickTime: Long = 0
    private val clickGap            = 500
    private var ticketList: ArrayList<MyTicketsListResponse.ResponseData.Ticket?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DgeTicketsViewHolder {
        val binding: RowTicketsDgeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_tickets_dge, parent, false)
        return DgeTicketsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DgeTicketsViewHolder, position: Int) {
        val row = holder.rowTicketsDgeBinding
        ticketList?.get(position)?.let { ticket ->
            row.tvLine3.text                        = getDisplayTransactionDate(ticket.drawDetails?.drawDateTime)
            row.tvTimeZone.text                     = TIME_ZONE
            row.tvCurrency.text                     = ticket.ticketDetails?.txnCurrency ?: context.getString(R.string.na)
            row.tvAmount.text                       = getFormattedAmount(ticket.ticketDetails?.saleAmount.toString().toDouble())
            row.tvTicketGameTicketNameInDge.text    = ticket.gameDisplayName
            ticket.ticketDetails?.winstatus?.let { status ->
                when (status) {
                    "SOLD", "RESULT_AWAITED" -> {
                        row.ivWinStatus.setImageResource(R.drawable.icon_wait_time)
                        row.tvStatusText.text = context.getString(R.string.result_awaited)
                        row.tvStatusText.setTextColor(Color.parseColor("#777777"))
                        noWinSetRowProperties(row)
                    }
                    "WIN", "CLAIMED" -> {
                        //row.ivWinStatus.setImageResource(R.drawable.icon_prize)
                        val currency    = ticket.ticketDetails.txnCurrency ?: ""
                        val amount      = getFormattedAmount(ticket.ticketDetails.winningAmount.toString().toDouble())
                        val text        = "$currency <b>$amount</b>"
                        row.tvCurrencyAndAmount.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        onWinSetRowProperties(row)
                        //row.tvStatusText.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        //row.tvStatusText.setTextColor(Color.parseColor("#4CAF50"))
                    }
                    "NON-WIN" -> {
                        row.ivWinStatus.setImageResource(R.drawable.icon_thumb_up)
                        row.tvStatusText.text = context.getString(R.string.better_luck)
                        row.tvStatusText.setTextColor(Color.parseColor("#777777"))
                        noWinSetRowProperties(row)
                    }
                    "CANCELLED" -> {
                        row.ivWinStatus.setImageResource(R.drawable.icon_cancel)
                        row.tvStatusText.text = context.getString(R.string.cancelled)
                        row.tvStatusText.setTextColor(Color.parseColor("#F44E42"))
                        noWinSetRowProperties(row)
                    }
                    else -> {
                        row.ivWinStatus.setImageResource(R.drawable.ic_icon_loading)
                        row.tvStatusText.text = context.getString(R.string.result_calculating)
                        row.tvStatusText.setTextColor(Color.parseColor("#777777"))
                        noWinSetRowProperties(row)
                    }
                }
            }

            row.llRow.setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                    return@setOnClickListener
                }
                lastClickTime = SystemClock.elapsedRealtime()

                onTicketClick(ticket)
            }
        }
    }

    private fun onWinSetRowProperties(row: RowTicketsDgeBinding) {
        row.llWonTicketLabel.visibility             = View.VISIBLE
        row.ivBoom5Icon.visibility                  = View.VISIBLE
        row.tvGameName.visibility                   = View.GONE
        row.tvTicketGameTicketNameInDge.visibility  = View.GONE
        row.tvStatusText.visibility                 = View.GONE
        row.ivWinStatus.visibility                  = View.GONE
    }

    private fun noWinSetRowProperties(row: RowTicketsDgeBinding) {
        row.llWonTicketLabel.visibility             = View.GONE
        row.ivBoom5Icon.visibility                  = View.GONE
        row.tvGameName.visibility                   = View.VISIBLE
        row.tvTicketGameTicketNameInDge.visibility  = View.VISIBLE
        row.tvStatusText.visibility                 = View.VISIBLE
        row.ivWinStatus.visibility                  = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return ticketList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun putTicketList(list: ArrayList<MyTicketsListResponse.ResponseData.Ticket?>) {
        ticketList = list
        notifyDataSetChanged()
    }

    private fun getDisplayTransactionDate(strDate: String?) : String {
        return if (strDate == null)
            context.getString(R.string.na)
        else
            getFormattedDateNew(strDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDateNew(transactionDate: String) : String {
        return try {
            //2021-08-31 12:15:08 ---O/P---> Aug 31 2021, 12:15 PM
            val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd yyyy, hh:mm aa")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: transactionDate
        } catch (e: Exception) {
            transactionDate
        }
    }

    class DgeTicketsViewHolder(val rowTicketsDgeBinding: RowTicketsDgeBinding) :
        RecyclerView.ViewHolder(rowTicketsDgeBinding.root)
}