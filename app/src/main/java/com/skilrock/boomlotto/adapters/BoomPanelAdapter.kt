package com.skilrock.boomlotto.adapters

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowBoomPanelBinding
import com.skilrock.boomlotto.models.BoomPanelData
import com.skilrock.boomlotto.utility.shakeError
import com.skilrock.boomlotto.viewmodels.BoomLottoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BoomPanelAdapter(val context: Context, val viewModel: BoomLottoViewModel, val lifecycleOwner: LifecycleOwner,
                       private val mPanelList: ArrayList<BoomPanelData>, private val mMaxSelectionCount: Int,
                       private val mMaxPanelAllowed: Int, val onBallClicked:()->Unit, val showAddLine:()->Unit) :
    RecyclerView.Adapter<BoomPanelAdapter.PanelBoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanelBoomViewHolder {
        val binding: RowBoomPanelBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_boom_panel, parent, false)
        binding.lifecycleOwner  = lifecycleOwner
        return PanelBoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PanelBoomViewHolder, position: Int) {
        holder.rowBoomPanelBinding.viewModel    = viewModel
        holder.rowBoomPanelBinding.model        = mPanelList[position]
        val row = holder.rowBoomPanelBinding
        row.cardRow.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        setQuickPick(position, row)

        val boomBallAdapter = BoomBallAdapter(context, mPanelList[position].listNumbers, mPanelList[position], mMaxSelectionCount, { onBallClicked() }) {
            if (it) {
                mPanelList[position].isQuickPick = true
                row.tvQuickPick.setBackgroundResource(R.drawable.selected_quick_pick)
                row.tvQuickPick.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                mPanelList[position].isQuickPick = false
                row.tvQuickPick.setBackgroundResource(R.drawable.select_draw_ripple)
                row.tvQuickPick.setTextColor(Color.parseColor("#FF0068"))
            }
        }

        row.rvNumbers.apply {
            layoutManager = GridLayoutManager(context, 6)
            setHasFixedSize(true)
            adapter = boomBallAdapter
        }

        val boomPanelBallAdapter = BoomPanelBallAdapter(mMaxSelectionCount)
        row.rvSelectedNumbers.apply {
            layoutManager = GridLayoutManager(context, mMaxSelectionCount)
            setHasFixedSize(true)
            adapter = boomPanelBallAdapter
        }

        if (mPanelList[position].isCardOpen)
            setPanelExpanded(position, row)
        else
            setPanelCollapsed(position, row, boomPanelBallAdapter)

        row.cardDelete.setOnClickListener {
            cardDeleteOperation(position, row, boomBallAdapter, boomPanelBallAdapter)
        }

        row.tvQuickPick.setOnClickListener {
            boomBallAdapter.quickPick()
        }

        row.llCard.setOnClickListener {
            onPanelClick(position)
        }
    }

    private fun setQuickPick(position: Int, row: RowBoomPanelBinding) {
        if (mPanelList[position].isQuickPickAllowed) {
            row.tvQuickPick.visibility = View.VISIBLE
            if (mPanelList[position].isQuickPick) {
                row.tvQuickPick.setBackgroundResource(R.drawable.selected_quick_pick)
                row.tvQuickPick.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                row.tvQuickPick.setBackgroundResource(R.drawable.select_draw_ripple)
                row.tvQuickPick.setTextColor(Color.parseColor("#FF0068"))
            }
        } else
            row.tvQuickPick.visibility = View.INVISIBLE
    }

    private fun cardDeleteOperation(position: Int, row: RowBoomPanelBinding, boomBallAdapter: BoomBallAdapter, boomPanelBallAdapter: BoomPanelBallAdapter) {
        val boomPanelData = mPanelList[position]

        if (boomPanelData.isCardOpen)
            deleteOperationWhenCardIsOpen(position, row, boomBallAdapter)
        else
            deleteOperationWhenCardIsClosed(position, row, boomBallAdapter, boomPanelBallAdapter)

        checkIfSingleCardLeft()
        resetLineNumber()
        checkIfAllCardsAreClosed()
        if (mPanelList.size < mMaxPanelAllowed) showAddLine()
    }

    private fun checkIfSingleCardLeft() {
        if (mPanelList.size == 1 && !mPanelList[0].isCardOpen) {
            mPanelList[0].isCardOpen = true
            //notifyItemChanged(0, mPanelList[0])
            notifyItemChanged(0)
        }
    }

    private fun checkIfAllCardsAreClosed() {
        if (mPanelList.none { it.isCardOpen }) {
            mPanelList[mPanelList.size - 1].isCardOpen = true
            notifyItemChanged(mPanelList.size - 1, mPanelList[mPanelList.size - 1])
        }
    }

    private fun deleteOperationWhenCardIsOpen(position: Int, row: RowBoomPanelBinding, boomBallAdapter: BoomBallAdapter) {
        val boomPanelData = mPanelList[position]
        if (mPanelList.size > 1) {
            if (boomPanelData.listNumbers.any { it.isClicked }) {
                boomBallAdapter.clearSelection()
                resetQuickPick(position, row)
            } else {
                mPanelList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeRemoved(position, mPanelList.size)
            }
            onBallClicked()
        } else {
            if (!boomBallAdapter.clearSelection()) {
                row.cardDelete.startAnimation(shakeError())
            } else {
                resetQuickPick(position, row)
                onBallClicked()
            }
        }
    }

    private fun deleteOperationWhenCardIsClosed(position: Int, row: RowBoomPanelBinding, boomBallAdapter: BoomBallAdapter, boomPanelBallAdapter: BoomPanelBallAdapter) {
        if (mPanelList.size > 1) {
            mPanelList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, mPanelList.size)
            onBallClicked()
        } else if (mPanelList.size == 1) {
            //mPanelList[0].isCardOpen = false
            onPanelClick(0)
            lifecycleOwner.lifecycleScope.launch {
                delay(300)
                if (!boomBallAdapter.clearSelection())
                    row.cardDelete.startAnimation(shakeError())
                else {
                    resetQuickPick(position, row)
                    onBallClicked()
                }
            }
        }
    }

    private fun resetLineNumber() {
        mPanelList.forEachIndexed { index, boomPanelData ->
            boomPanelData.indexLiveData.value = context.getString(R.string.line) + " " + (index + 1)
        }
    }

    private fun resetQuickPick(position: Int, row: RowBoomPanelBinding) {
        mPanelList[position].isQuickPick = false
        row.tvQuickPick.setBackgroundResource(R.drawable.select_draw_ripple)
        row.tvQuickPick.setTextColor(Color.parseColor("#FF0068"))
    }

    private fun onPanelClick(position: Int) {
        var openedCardPosition = -1
        mPanelList.forEachIndexed { index, boomPanelData -> if (boomPanelData.isCardOpen) openedCardPosition = index }

        if (openedCardPosition != position) {
            mPanelList.forEachIndexed { index, panelData ->
                panelData.isCardOpen = index == position
                notifyItemChanged(index)
            }
        }
        /*if (mPanelList[position].isCardOpen)
            setPanelCollapsed(position, row, boomPanelBallAdapter)
        else
            setPanelExpanded(position, row)*/
    }

    private fun setPanelCollapsed(position: Int, row: RowBoomPanelBinding, boomPanelBallAdapter: BoomPanelBallAdapter) {
        row.rvNumbers.visibility            = View.GONE
        row.rvSelectedNumbers.visibility    = View.VISIBLE

        val list = ArrayList<String>()
        mPanelList[position].listNumbers.forEach { ballInfo ->
            if (ballInfo.isClicked)
                list.add(ballInfo.number)
        }
        boomPanelBallAdapter.setBallList(list)
        //row.rvSelectedNumbers.scheduleLayoutAnimation()

        row.llRowQuickPick.visibility       = View.GONE
        mPanelList[position].isCardOpen     = false
        row.tvPickInstruction.animate().alpha(0f).duration = 400
    }

    private fun setPanelExpanded(position: Int, row: RowBoomPanelBinding) {
        row.rvNumbers.visibility            = View.VISIBLE
        row.rvSelectedNumbers.visibility    = View.GONE
        row.llRowQuickPick.visibility       = View.VISIBLE
        mPanelList[position].isCardOpen     = true
        //row.rvNumbers.scheduleLayoutAnimation()
        row.tvPickInstruction.animate().alpha(1f).duration = 400
    }

    override fun getItemCount(): Int {
        return mPanelList.size
    }

    class PanelBoomViewHolder(val rowBoomPanelBinding: RowBoomPanelBinding) :
    RecyclerView.ViewHolder(rowBoomPanelBinding.root)

}