package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowDrawBinding
import com.skilrock.boomlotto.dialogs.DrawSelectionSheet

class BoomDrawSelectionAdapter(val context: Context, private val drawList: ArrayList<DrawSelectionSheet.DrawData>, private val onDrawSelected:(Int)->Unit) :
    RecyclerView.Adapter<BoomDrawSelectionAdapter.DrawSelectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawSelectionViewHolder {
        val binding: RowDrawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_draw, parent, false)
        return DrawSelectionViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: DrawSelectionViewHolder, position: Int) {
        val drawData = drawList[position]

        if (drawData.isSelected) {
            holder.rowDrawBinding.llDraw.setBackgroundResource(R.drawable.draw_box_selected)
            holder.rowDrawBinding.tvDraw.setTextColor(Color.parseColor("#FF0068"))
        } else {
            holder.rowDrawBinding.llDraw.setBackgroundResource(R.drawable.draw_box_unselected)
            holder.rowDrawBinding.tvDraw.setTextColor(Color.parseColor("#00004C"))
        }

        holder.rowDrawBinding.tvDraw.text = HtmlCompat.fromHtml(drawList[position].text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        holder.rowDrawBinding.llDraw.setOnClickListener {
            onDrawSelected(drawData.position)
            drawList.forEachIndexed { index, drawData ->
                drawData.isSelected = false
                notifyItemChanged(index, drawData)
            }

            drawList[position].isSelected = true
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return drawList.size
    }

    class DrawSelectionViewHolder(val rowDrawBinding: RowDrawBinding) :
        RecyclerView.ViewHolder(rowDrawBinding.root)

}