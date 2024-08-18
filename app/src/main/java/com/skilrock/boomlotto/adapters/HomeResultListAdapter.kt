package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowHomeResultListBinding
import com.skilrock.boomlotto.models.response.ResultResponse
import java.text.SimpleDateFormat

class HomeResultListAdapter(val context: Context, private var resultList: ArrayList<ResultResponse.ResponseData.ResultData.ResultInfo>)  :
    RecyclerView.Adapter<HomeResultListAdapter.HomeResultListViewHolder>() {

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): HomeResultListViewHolder {
        val binding: RowHomeResultListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_home_result_list, parent, false)
        return HomeResultListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeResultListViewHolder, position: Int) {
        val balls =  resultList[position].winningNo
        val row = holder.rowHomeResultListBinding
        val resultInfo  = resultList[position]

        resultInfo.resultDate?.let {
            row.tvDay.text  = getDayName(it)
            row.tvDateAndTime.text = ("${getNewAbbreviatedFromDate(it)}${if(resultInfo.drawTime != null)", " + getAbbreviatedFromTime(resultInfo.drawTime) else ""}")
        }

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
    }

    override fun getItemCount(): Int {
        return resultList.size
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
        return context.getString(R.string.na)
    }

    @SuppressLint("SimpleDateFormat")
    fun getNewAbbreviatedFromDate(dateTime: String): String? {
        val input   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output  = SimpleDateFormat("MMM dd")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return context.getString(R.string.na)
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
        return context.getString(R.string.na)
    }

    class HomeResultListViewHolder(val rowHomeResultListBinding: RowHomeResultListBinding) :
        RecyclerView.ViewHolder(rowHomeResultListBinding.root)

}