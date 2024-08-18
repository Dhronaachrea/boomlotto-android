package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowInboxBinding
import com.skilrock.boomlotto.models.response.PlayerInboxResponse
import java.util.*
import kotlin.collections.ArrayList


class InboxAdapter(
    private val context: Context, private val onClickMail: (PlayerInboxResponse.PlrInbox, Int) -> Unit,
    private val onLongPressMail: (PlayerInboxResponse.PlrInbox) -> Unit
):
    RecyclerView.Adapter<InboxAdapter.InboxViewHolder>() {

    companion object {
        var IS_LONG_PRESS_ACTIVATED = false
    }

    private var listMail: ArrayList<PlayerInboxResponse.PlrInbox?>? = null
    private var listMailCopy: ArrayList<PlayerInboxResponse.PlrInbox?>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
        val binding: RowInboxBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_inbox, parent, false)

        return InboxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
        holder.rowInboxBinding.inboxData = listMail?.get(position)
        holder.rowInboxBinding.containerInboxRow.setOnClickListener {
            holder.rowInboxBinding.inboxData?.let {
                if (IS_LONG_PRESS_ACTIVATED) {
                    it.isSelectedByLongPress = !it.isSelectedByLongPress
                    notifyItemChanged(position)
                    onLongPressMail(it)
                } else
                    onClickMail(it, position)
            }
        }

        holder.rowInboxBinding.containerInboxRow.setOnLongClickListener {
            holder.rowInboxBinding.inboxData?.let {
                it.isSelectedByLongPress = !it.isSelectedByLongPress
                notifyItemChanged(position)
                onLongPressMail(it)
            }
            true
        }

        holder.rowInboxBinding.inboxData?.let { data ->
            setInboxUiAndData(holder, data)
        }
    }

    private fun setInboxUiAndData(holder: InboxViewHolder, data: PlayerInboxResponse.PlrInbox) {
        if (data.isMsgRead()) {
            holder.rowInboxBinding.tvInboxSubject.setTypeface(null, Typeface.NORMAL)
            holder.rowInboxBinding.tvInboxSubject.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            //holder.rowInboxBinding.ivIcon.setImageResource(R.drawable.icon_message_read)
            holder.rowInboxBinding.containerInboxRow.setBackgroundColor(Color.parseColor("#f5f7f9"))
        } else {
            holder.rowInboxBinding.tvInboxSubject.setTypeface(null, Typeface.BOLD)
            holder.rowInboxBinding.tvInboxSubject.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            //holder.rowInboxBinding.ivIcon.setImageResource(R.drawable.icon_message_unread)
            holder.rowInboxBinding.containerInboxRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        if (holder.rowInboxBinding.inboxData?.isSelectedByLongPress == true) {
            holder.rowInboxBinding.ivCheck.visibility = View.VISIBLE
            holder.rowInboxBinding.containerInboxRow.setBackgroundColor(Color.parseColor("#e0e0e0"))
        } else {
            holder.rowInboxBinding.ivCheck.visibility = View.GONE
            holder.rowInboxBinding.containerInboxRow.setBackgroundColor(ContextCompat.getColor(context, R.color.fragment_background_color))
        }
    }

    fun readMessage(inboxId: Int, position: Int) {
        listMail?.get(position)?.let { messageData ->
            if (messageData.inboxId == inboxId) {
                messageData.status = "READ"
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listMail?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setInboxList(list: ArrayList<PlayerInboxResponse.PlrInbox?>?) {
        listMail = list
        listMailCopy?.clear()
        list?.let {
            for (mail in it) { listMailCopy?.add(mail) }
        }
        IS_LONG_PRESS_ACTIVATED = false
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetLongPressRows() {
        IS_LONG_PRESS_ACTIVATED = false
        for (index in 0 until (listMail?.size ?: 0))
            listMail?.get(index)?.isSelectedByLongPress = false

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll(flag: Boolean) {
        for (index in 0 until (listMail?.size ?: 0))
            listMail?.get(index)?.isSelectedByLongPress = flag

        notifyDataSetChanged()
    }

    fun getAllInboxId() : ArrayList<Int> {
        val list = ArrayList<Int>()
        for (index in 0 until (listMail?.size ?: 0)) {
            listMail?.get(index)?.inboxId?.let { id ->
                list.add(id)
            }
        }
        return list
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        listMail?.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun searchFilter(text: String) {
        if (text.isBlank()) {
            listMail = listMailCopy
            notifyDataSetChanged()
        } else {
            val filteredList = ArrayList<PlayerInboxResponse.PlrInbox?>()
            listMailCopy?.let {
                for (mail in it) {
                    if ((mail?.subject?.lowercase(Locale.ROOT)
                            ?.contains(text.lowercase(Locale.ROOT)) == true) or (mail?.contentIdForUse?.lowercase(Locale.ROOT)
                            ?.contains(text.lowercase(Locale.ROOT)) == true) ) {
                        filteredList.add(mail)
                    }
                }
                listMail = filteredList
                notifyDataSetChanged()
            }
        }
    }

    fun getInboxId(position: Int): Int? {
        return listMail?.get(position)?.inboxId
    }

    override fun getItemId(position: Int): Long {
        return listMail?.get(position)?.inboxId?.toLong() ?: -1
    }

    class InboxViewHolder(val rowInboxBinding: RowInboxBinding) :
        RecyclerView.ViewHolder(rowInboxBinding.root)
}