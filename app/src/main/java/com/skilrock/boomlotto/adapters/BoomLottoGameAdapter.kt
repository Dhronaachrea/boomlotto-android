package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowBoomGameBallBinding
import com.skilrock.boomlotto.models.response.BoomLottoGameResponse.ResponseData.GameRespVO.NumberConfig.Range.Ball

class BoomLottoGameAdapter(private val onBallClick:(Ball)->Unit, private val disruptLineFinish:()->Unit) :
    RecyclerView.Adapter<BoomLottoGameAdapter.BallBoomViewHolder>() {

    private var lastClickTime: Long = 0
    private val clickGap            = 400
    private var isClickAllowed      = true

    private var ballList: ArrayList<Ball?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BallBoomViewHolder {
        val binding: RowBoomGameBallBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_boom_game_ball, parent, false)
        return BallBoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BallBoomViewHolder, position: Int) {
        val ballData = ballList?.get(position)
        holder.rowBoomGameBallBinding.model = ballData
        val tvBall = holder.rowBoomGameBallBinding.tvBall

        if (ballData?.isResetAnimation == true) {
            ballSelection(tvBall, false, position, ballData, true)
            ballData.isResetAnimation = false
        } else if (ballData?.isQuickPickAnimation == true) {
            if (ballData.isClicked) {
                if (isClickAllowed)
                    ballSelection(tvBall, true, position, ballData)
            }
            else {
                ballSelection(tvBall, false, position, ballData)
            }
            ballData.isQuickPickAnimation = false
        } else {
            if (ballData?.isClicked == true)
                ballSelectionWithoutAnimation(tvBall, true)
            else
                ballSelectionWithoutAnimation(tvBall, false)
        }

        ballClickListener(tvBall, ballData, position)
    }

    private fun ballClickListener(tvBall: MaterialTextView, ballData: Ball?, position: Int) {
        tvBall.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            if (ballData?.isClicked == true) {
                ballSelection(tvBall, false, position, ballData)
            }
            else {
                if (isClickAllowed)
                    ballSelection(tvBall, true, position, ballData)
            }
        }
    }

    private fun ballSelectionWithoutAnimation(tvBall: MaterialTextView, isSelected: Boolean) {
        tvBall.setBackgroundResource(if (isSelected) R.drawable.selected_ball else R.drawable.unselected_ball)
        val color = if (isSelected) Color.parseColor("#ffffff") else Color.parseColor("#333333")
        tvBall.setTextColor(color)
        tvBall.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun ballSelection(tvBall: MaterialTextView, isSelected: Boolean, position: Int, ballData: Ball?, isResetGame: Boolean = false) {
        tvBall.animate()
            .scaleX(0f).scaleY(0f)
            .setDuration(200)
            .withEndAction {
                if (!isSelected)
                    disruptLineFinish()
                ballData?.isClicked = isSelected
                notifyItemChanged(position, ballData)
                ballData?.let {
                    if (!isResetGame)
                        onBallClick(it)
                }

                tvBall.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .withEndAction { }
            }
    }

    override fun getItemCount(): Int {
        return ballList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBallsList(list: ArrayList<Ball?>?) {
        ballList = list
        notifyDataSetChanged()
    }

    class BallBoomViewHolder(val rowBoomGameBallBinding: RowBoomGameBallBinding) :
        RecyclerView.ViewHolder(rowBoomGameBallBinding.root)

}