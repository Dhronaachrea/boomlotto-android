package com.skilrock.boomlotto.utility

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.skilrock.boomlotto.R
import java.text.SimpleDateFormat
import java.util.*

fun openStartDateDialog(context: Context, tvStartDate: TextView, tvEndDate: TextView) {
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mYearEnd: Int
    val mMonthEnd: Int
    val mDayEnd: Int
    val arrDate =
        getDdMmYyyy(tvStartDate.text.toString()).trim { it <= ' ' }.split("-").toTypedArray()
    mYear = arrDate[2].toInt()
    mMonth = arrDate[1].toInt() - 1
    mDay = arrDate[0].toInt()
    val arrDateEnd =
        getDdMmYyyy(tvEndDate.text.toString()).trim { it <= ' ' }.split("-").toTypedArray()
    mYearEnd = arrDateEnd[2].toInt()
    mMonthEnd = arrDateEnd[1].toInt() - 1
    mDayEnd = arrDateEnd[0].toInt()
    val dialogStartDate = DatePickerDialog(context,
        R.style.date_picker_theme, { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"
            tvStartDate.text = getMmmDdYyyy(date)
        },
        mYear,
        mMonth,
        mDay)
    calender[mYearEnd, mMonthEnd, mDayEnd, 0] = 0
    dialogStartDate.datePicker.maxDate = calender.timeInMillis
    dialogStartDate.window?.attributes?.windowAnimations = R.style.DialogAnimationLeftToCenter
    dialogStartDate.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_corners_date_picker))
    dialogStartDate.show()
    dialogStartDate.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.teal_a700))
}

fun openEndDateDialog(context: Context, tvStartDate: TextView, tvEndDate: TextView) {
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mYearStart: Int
    val mMonthStart: Int
    val mDayStart: Int
    val arrDate =
        getDdMmYyyy(tvEndDate.text.toString()).trim { it <= ' ' }.split("-").toTypedArray()
    mYear = arrDate[2].toInt()
    mMonth = arrDate[1].toInt() - 1
    mDay = arrDate[0].toInt()
    val arrDateStart =
        getDdMmYyyy(tvStartDate.text.toString()).trim { it <= ' ' }.split("-").toTypedArray()
    mYearStart = arrDateStart[2].toInt()
    mMonthStart = arrDateStart[1].toInt() - 1
    mDayStart = arrDateStart[0].toInt()
    val dialogEndDate = DatePickerDialog(context,
        R.style.date_picker_theme, { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"
            tvEndDate.text = getMmmDdYyyy(date)
        },
        mYear,
        mMonth,
        mDay)
    dialogEndDate.datePicker.maxDate = Date().time
    calender[mYearStart, mMonthStart, mDayStart, 0] = 0
    dialogEndDate.datePicker.minDate = calender.timeInMillis
    dialogEndDate.window?.attributes?.windowAnimations = R.style.DialogAnimationRightToCenter
    dialogEndDate.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_corners_date_picker))
    dialogEndDate.show()
    dialogEndDate.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.teal_a700))
}

@SuppressLint("SimpleDateFormat")
fun openDobDialog(context: Context, onDateSelected:(String)->Unit) {
    Log.d("log", "Eighteen: " + getPreviousDateYMD(6574))
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mYearEnd: Int
    val mMonthEnd: Int
    val mDayEnd: Int

    val date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

    val arrDateStart = df.format(date).split("-").toTypedArray()
    mYear   = arrDateStart[2].toInt()
    mMonth  = arrDateStart[1].toInt() - 1
    mDay    = arrDateStart[0].toInt()
    val arrDateEnd = getPreviousDateYMD(6574).split("-").toTypedArray()
    mYearEnd    = arrDateEnd[0].toInt()
    mMonthEnd   = arrDateEnd[1].toInt() - 1
    mDayEnd     = arrDateEnd[2].toInt()
    val dialogStartDate = DatePickerDialog(
        context,
        { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"

            try {
                val parser = SimpleDateFormat("dd-MM-yyyy")
                val formatter = SimpleDateFormat("MMM dd, yyyy")
                val parse = parser.parse(date)
                parse?.let {
                        parsedDate -> onDateSelected(formatter.format(parsedDate))
                } ?: run {
                    onDateSelected(date)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onDateSelected(date)
            }
        },
        mYear,
        mMonth,
        mDay
    )
    calender[mYearEnd, mMonthEnd, mDayEnd, 0] = 0
    dialogStartDate.datePicker.maxDate = calender.timeInMillis
    dialogStartDate.window?.attributes?.windowAnimations = R.style.DialogAnimationRightToCenter
    dialogStartDate.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_corners_date_picker))
    dialogStartDate.show()
    dialogStartDate.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.color_app_pink))
}

@SuppressLint("SimpleDateFormat")
fun openIdExpiryDialog(context: Context, onDateSelected:(String)->Unit) {
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

    val arrDateStart = df.format(date).split("-").toTypedArray()
    mYear   = arrDateStart[2].toInt()
    mMonth  = arrDateStart[1].toInt() - 1
    mDay    = arrDateStart[0].toInt()
    val dialogStartDate = DatePickerDialog(context,
        { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"

            try {
                val parser = SimpleDateFormat("dd-MM-yyyy")
                val formatter = SimpleDateFormat("MMM dd, yyyy")
                val parse = parser.parse(date)
                parse?.let {
                        parsedDate -> onDateSelected(formatter.format(parsedDate))
                } ?: run {
                    onDateSelected(date)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onDateSelected(date)
            }
        }, mYear, mMonth, mDay)
    dialogStartDate.datePicker.minDate = calender.timeInMillis - 1000
    dialogStartDate.window?.attributes?.windowAnimations = R.style.DialogAnimationRightToCenter
    dialogStartDate.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_corners_date_picker))
    dialogStartDate.show()
    dialogStartDate.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.color_app_pink))
}