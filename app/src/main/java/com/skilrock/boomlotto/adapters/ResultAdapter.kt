package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowResultBinding
import com.skilrock.boomlotto.models.response.ResultResponse
import java.text.SimpleDateFormat

class ResultAdapter(val context: Context) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    private var listResults: ArrayList<ResultResponse.ResponseData.ResultData.ResultInfo>? = null
    private var lastClickTime: Long = 0
    private val clickGap            = 400

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding: RowResultBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_result, parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val row         = holder.rowResultBinding
        val resultInfo  = listResults?.get(position)

        resultInfo?.resultDate?.let {
            row.tvDay.text  = getDayName(it)
            row.tvDate.text = ("${getNewAbbreviatedFromDate(it)}${if(resultInfo.drawTime != null)", " + getAbbreviatedFromTime(resultInfo.drawTime) else ""}")
        } ?: run {
            row.tvDay.text  = "--"
            row.tvDate.text = "--"
        }

        resultInfo?.drawId?.let { row.tvDrawId.text = it.toString() } ?: run { row.tvDrawId.text = "--" }
        row.tvJackpotTitle.text     = (context.getString(R.string.jackpot_small))
        row.tvJackpotAmount.text    = "--"

        val balls = resultInfo?.winningNo

        if (balls == null || balls.isBlank()) {
            row.llBalls.visibility = View.GONE
        } else {
            val ballList = balls.split(",")
            if (ballList.isNotEmpty() && ballList.size == 5) {
                row.tvBall1.text = ballList[0]
                row.tvBall2.text = ballList[1]
                row.tvBall3.text = ballList[2]
                row.tvBall4.text = ballList[3]
                row.tvBall5.text = ballList[4]
            } else
                row.llBalls.visibility = View.GONE
        }

        resultInfo?.matchInfo?.also {
            setMatchInfoRecyclerView(row.rvDetails, it)

            if (resultInfo.isExpanded) {
                row.llMoreTextAndDownIcon.visibility = View.GONE
                row.llMatches.visibility = View.VISIBLE
            }
            else {
                row.llMatches.visibility = View.GONE
                row.llMoreTextAndDownIcon.visibility = View.VISIBLE
            }
        }

        row.llContainer.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            resultInfo?.let { info ->
                var index = -1
                val expandedItem = listResults?.find { result -> result.isExpanded }
                if (expandedItem != null) {
                    expandedItem.isExpanded = false
                    notifyItemChanged(expandedItem.rowIndex)
                    index = expandedItem.rowIndex
                }

                if (index != position) {
                    info.isExpanded = !info.isExpanded
                    notifyItemChanged(position)
                }
            }
        }
    }

    private fun setMatchInfoRecyclerView(recyclerView: RecyclerView, matchList: ArrayList<ResultResponse.ResponseData.ResultData.ResultInfo.MatchInfo?>) {
        matchList.forEachIndexed { index, matchInfo -> matchInfo?.rowIndex = index }
        val matchAdapter = ResultMatchAdapter(context, matchList)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = matchAdapter
        }
    }

    override fun getItemCount(): Int {
        return listResults?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setResultListData(list: ArrayList<ResultResponse.ResponseData.ResultData.ResultInfo>) {
        listResults = list
        notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDayName(dateTime: String): String {
        val input   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output  = SimpleDateFormat("EEEE")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return "--"
    }

    @SuppressLint("SimpleDateFormat")
    fun getNewAbbreviatedFromDate(dateTime: String): String? {
        val input   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output  = SimpleDateFormat("MMM dd yyyy")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return "--"
    }

    @SuppressLint("SimpleDateFormat")
    fun getAbbreviatedFromTime(dateTime: String): String? {
        val input   = SimpleDateFormat("HH:mm:ss")
        val output  = SimpleDateFormat("hh:mm aa")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return "--"
    }

    class ResultViewHolder(val rowResultBinding: RowResultBinding) : RecyclerView.ViewHolder(rowResultBinding.root)
}