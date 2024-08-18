package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowStateBinding
import com.skilrock.boomlotto.models.response.StateResponse

class StateAdapter(private val onStateSelected:(StateResponse.Data)->Unit) : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {

    private var stateList: ArrayList<StateResponse.Data?>? = null
    private var stateListCopy: ArrayList<StateResponse.Data?>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val binding: RowStateBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_state, parent, false)
        return StateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        holder.rowStateBinding.model = stateList?.get(position)
        holder.rowStateBinding.llRow.setOnClickListener {
            stateList?.let { list -> list[position]?.let { onStateSelected(it) } }
        }
    }

    override fun getItemCount(): Int {
        return stateList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setStateList(list: ArrayList<StateResponse.Data?>?) {
        stateList = list
        stateListCopy?.clear()
        list?.let {
            for (state in it) { stateListCopy?.add(state) }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun searchFilter(text: String) {
        if (text.isBlank()) {
            stateList = stateListCopy
            notifyDataSetChanged()
        } else {
            val filteredList = ArrayList<StateResponse.Data?>()
            stateListCopy?.let {
                for (state in it) {
                    if ((state?.stateName?.lowercase()?.contains(text.lowercase()) == true) or
                        (state?.stateCode?.lowercase()?.contains(text.lowercase()) == true)) {
                        filteredList.add(state)
                    }
                }
                stateList = filteredList
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return stateList?.get(position)?.stateCode?.toLong() ?: -1
    }

    class StateViewHolder(val rowStateBinding: RowStateBinding) :
        RecyclerView.ViewHolder(rowStateBinding.root)

}