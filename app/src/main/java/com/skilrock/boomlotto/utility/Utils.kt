package com.skilrock.boomlotto.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


fun shakeError(): TranslateAnimation {
    val shake = TranslateAnimation(0f, 10f, 0f, 0f)
    shake.duration = 500
    shake.interpolator = CycleInterpolator(7f)
    return shake
}

fun vibrate(context: Context) {
    val vibrator = getSystemService(context, Vibrator::class.java)
    vibrator?.let {
        if (Build.VERSION.SDK_INT >= 26) {
            it.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            it.vibrate(80)
        }
    }
}

infix fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun AppCompatEditText.getTrimText() : String {
    return text.toString().trim()
}

fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}

fun getPreviousDate(days: Int): String {
    val cal = Calendar.getInstance()
    cal.time = Date()
    cal.add(Calendar.DATE, -days)
    val date = cal.time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    return df.format(date)
}

fun getCurrentDate(): String {
    val date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    //dd-MM-yyyy
    return df.format(date)
}

@SuppressLint("SimpleDateFormat")
fun getDdMmYyyy(sourceDate: String) : String {
    val input   = SimpleDateFormat("MMM dd, yyyy")
    val output  = SimpleDateFormat("dd-MM-yyyy")
    try {
        input.parse(sourceDate)?.let {
            return output.format(it)
        }
    } catch (e: Exception) {
        Log.e("log", "Date parsing error: ${e.message}")
    }
    return ""
}

@SuppressLint("SimpleDateFormat")
fun getMmmDdYyyy(sourceDate: String) : String {
    val input   = SimpleDateFormat("dd-MM-yyyy")
    val output  = SimpleDateFormat("MMM dd, yyyy")
    try {
        input.parse(sourceDate)?.let {
            return output.format(it)
        }
    } catch (e: Exception) {
        Log.e("log", "Date parsing error: ${e.message}")
    }
    return ""
}

fun getPreviousDateYMD(days: Int): String {
    val cal = Calendar.getInstance()
    cal.time = Date()
    cal.add(Calendar.DATE, -days)
    val date = cal.time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return df.format(date)
}

fun getCurrentDateYMD(): String {
    val date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat") val df =
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return df.format(date)
}

fun performLogoutCleanUp(activity: Activity, intent: Intent) {
    PlayerInfo.destroy()
    SharedPrefUtils.clearAppSharedPref(activity)
    activity.finish()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun TextInputLayout.putError(errorMsg: String) {
    error = errorMsg
    requestFocus()
    startAnimation(shakeError())
}

fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme.equals("content")) {
        context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                result = cursor.getString(columnIndex)
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

fun getFormattedAmount(amount: Double): String {
    var formattedAmount = amount.toString()
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("AED")

    format.currency?.let{
        val amt = format.format(amount)
        formattedAmount = amt.replace(it.symbol, "")
    }
    return formattedAmount
}
