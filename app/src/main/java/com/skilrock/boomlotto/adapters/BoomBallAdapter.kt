package com.skilrock.boomlotto.adapters

import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowBoomBallBinding
import com.skilrock.boomlotto.models.BoomPanelData
import java.util.*
import kotlin.collections.ArrayList

class BoomBallAdapter(val context: Context, private val mBallList: ArrayList<BoomPanelData.BallInfo>, private val mPanelData: BoomPanelData,
                      private val mMaxSelectionCount: Int, val onBallClicked:()->Unit, val quickPickListener:(Boolean)->Unit) : RecyclerView.Adapter<BoomBallAdapter.BallViewHolder>()  {

    private var lastClickTime: Long     = 0
    private val clickGap                = 400
    private var isDelete                = false
    private var mListTextViews          = ArrayList<MaterialTextView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BallViewHolder {
        val binding: RowBoomBallBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_boom_ball, parent, false)
        mListTextViews.add(binding.tvBall)
        return BallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BallViewHolder, position: Int) {
        val ballData                    = mBallList[position]
        val tvBall                      = holder.rowBoomBallBinding.tvBall
        holder.rowBoomBallBinding.model = ballData

        when {
            ballData.isClicked -> {
                tvBall.setBackgroundResource(R.drawable.selected_ball)
                tvBall.setTextColor(ContextCompat.getColor(context, R.color.color_app_pink))
            }
            isDelete -> {
                ballAnimationDelete(tvBall)
            }
            mPanelData.reachedMaxSelectionCount -> {
                tvBall.setBackgroundResource(R.drawable.disabled_ball)
                tvBall.setTextColor(Color.parseColor("#A2A2A2"))
            }

            else -> {
                tvBall.setBackgroundResource(R.drawable.unselected_ball)
                tvBall.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
            }
        }

        tvBall.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            isDelete = false
            if (ballData.isClicked) {
                ballData.isClicked = false
                quickPickListener(false)
                ballAnimation(tvBall, position, ballData)
            } else {
                if (!mPanelData.reachedMaxSelectionCount) {
                    //mBallList.forEach { if (it.isClicked) count++ }
                    val count = mBallList.count { ball -> ball.isClicked }
                    if (count < 6) {
                        ballData.isClicked = true
                        ballAnimation(tvBall, position, ballData)
                    }
                }
            }
        }
    }

    fun quickPick() {
        val randomNumbers = LinkedHashSet<String>()
        while (randomNumbers.size != mMaxSelectionCount) {
            randomNumbers.add((1..30).random().toString())
        }

        val listQuickPick = ArrayList(randomNumbers)
        for (index in 0 until listQuickPick.size) {
            if (listQuickPick[index].toInt() < 10) {
                listQuickPick[index] = "0${listQuickPick[index]}"
            }
        }

        Log.d("log", "Quick Pick Numbers: $listQuickPick")
        clearSelection()
        mBallList.forEachIndexed { index, ballInfo ->
            if (listQuickPick.contains(ballInfo.number)) {
                quickPickClick(mBallList[index], mListTextViews[index], index)
            }
        }
        quickPickListener(true)
    }

    private fun quickPickClick(ballData: BoomPanelData.BallInfo, tvBall: MaterialTextView, position: Int) {
        isDelete = false
        if (ballData.isClicked) {
            ballData.isClicked = false
            quickPickListener(false)
            ballAnimation(tvBall, position, ballData)
        } else {
            if (!mPanelData.reachedMaxSelectionCount) {
                ballData.isClicked = true
                ballAnimation(tvBall, position, ballData)
            }
        }
    }

    fun clearSelection() : Boolean {
        if (mBallList.any { it.isClicked }) {
            isDelete    = true
            mPanelData.reachedMaxSelectionCount = false

            mBallList.forEachIndexed { index, ballInfo ->
                ballInfo.isClicked = false
                notifyItemChanged(index, ballInfo)
            }
            return true
        }
        return false
    }

    private fun ballAnimationDelete(tvBall: MaterialTextView) {
        tvBall.animate()
            .scaleX(0f).scaleY(1f)
            .setDuration(200)
            .withEndAction {
                tvBall.setBackgroundResource(R.drawable.unselected_ball)
                tvBall.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
                tvBall.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .withEndAction {  }
            }
    }

    private fun ballAnimation(tvBall: MaterialTextView, position: Int, ballData: BoomPanelData.BallInfo) {
        tvBall.animate()
            .scaleX(0f).scaleY(0f)
            .setDuration(200)
            .withEndAction {
                notifyItemChanged(position, ballData)
                tvBall.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .withEndAction {
                        //isClickAllowed = true
                        selectedBallCounting()
                    }
            }
    }

    private fun selectedBallCounting() {
        var count = 0
        mBallList.forEach { if (it.isClicked) count++ }
        mPanelData.reachedMaxSelectionCount = count == mMaxSelectionCount

        mBallList.forEachIndexed { index, ballInfo -> notifyItemChanged(index, ballInfo) }
        onBallClicked()
    }

    override fun getItemId(position: Int): Long {
        return mBallList[position].number.toLong()
    }

    override fun getItemCount(): Int {
        return mBallList.size
    }

    class BallViewHolder(val rowBoomBallBinding: RowBoomBallBinding) :
    RecyclerView.ViewHolder(rowBoomBallBinding.root)
}