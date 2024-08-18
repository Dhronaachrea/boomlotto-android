package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowBoomPanelTrashBinding
import com.skilrock.boomlotto.models.BoomPanelDataTrash
import com.skilrock.boomlotto.utility.showToast

class BoomPanelAdapterTrash(val context: Context, val updateBuyAmount:()->Unit): RecyclerView.Adapter<BoomPanelAdapterTrash.PanelViewHolder>() {

    private var listPanelTrash: ArrayList<BoomPanelDataTrash>? = null
    lateinit var recyclerView: RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanelViewHolder {
        val binding: RowBoomPanelTrashBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_boom_panel_trash, parent, false)
        return PanelViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PanelViewHolder, position: Int) {
        if (position == (itemCount -1)) {
            recyclerView = holder.rowBoomPanelBinding.rvRow
        }

        holder.rowBoomPanelBinding.tvIndex.text = listPanelTrash?.get(position)?.index.toString()

        listPanelTrash?.get(position)?.listNumbers?.let {
            /*val ballAdapter = BoomPanelBallAdapter()
            holder.rowBoomPanelBinding.rvRow.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = ballAdapter
            }*/
            //ballAdapter.setBallList(it)
        }

        holder.rowBoomPanelBinding.ivTrash.setOnClickListener {
            listPanelTrash?.let { list: ArrayList<BoomPanelDataTrash> ->
                val listNumbers: ArrayList<BoomPanelDataTrash.BallInfo> = list[position].listNumbers
                var isDeletable = true
                for (index in 0 until listNumbers.size) {
                    if (listNumbers[index].number.isBlank()) {
                        isDeletable = false
                        break
                    }
                }

                if (isDeletable && list.size > 1) {
                    try {
                        list.remove(list[position])
                        list.forEachIndexed { index, boomPanelData ->
                            boomPanelData.index = index + 1
                            boomPanelData.listNumbers.forEach {
                                it.rowIndex = boomPanelData.index
                            }
                        }

                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, list.size)
                    } catch (e: Exception) {
                        context.showToast(context.getString(R.string.some_problem_occurred))
                        Log.e("log", "Exception: ${e.message}")
                    }
                } else
                    context.showToast(context.getString(R.string.complete_line_to_delete))
            }
            updateBuyAmount()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPanelList(list: ArrayList<BoomPanelDataTrash>) {
        listPanelTrash = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listPanelTrash?.size ?: 0
    }

    class PanelViewHolder(val rowBoomPanelBinding: RowBoomPanelTrashBinding) :
        RecyclerView.ViewHolder(rowBoomPanelBinding.root)

}