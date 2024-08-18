package com.skilrock.boomlotto.utility

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.WindowManager
import com.skilrock.boomlotto.R

object Loader {

    private var dialog: ProgressDialog? = null

    fun showLoader(context: Context?) {
        dismiss()
        dialog = ProgressDialog(context)

        dialog?.let { pDialog ->
            val window = pDialog.window
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.attributes?.windowAnimations = R.style.DialogAnimationFadeInOut
            pDialog.isIndeterminate = true
            pDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            pDialog.setCancelable(false)
            pDialog.show()
            pDialog.setContentView(R.layout.loader_layout)
            countDownTimer.start()
        }
    }

    fun dismiss() {
        dialog?.let {
            if (it.isShowing) {
                it.dismiss()
                it.cancel()
            }
        }
    }

    private val countDownTimer: CountDownTimer = object : CountDownTimer(1000 * 59, 3200) {
        override fun onTick(millisUntilFinished: Long) {
            //Gets called on every second
        }

        override fun onFinish() {
            dismiss()
        }
    }

}