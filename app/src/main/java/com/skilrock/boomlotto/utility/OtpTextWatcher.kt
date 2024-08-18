package com.skilrock.boomlotto.utility

import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText
import com.skilrock.boomlotto.R

class OtpTextWatcher(private val editText: AppCompatEditText,
                     private val listEditText: List<AppCompatEditText>,
                     private val setLoginButtonMode:(Boolean, Boolean)->Unit) : TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(editable: Editable?) {
        val text = editable.toString()
        when (editText.id) {
            R.id.etOtp1 -> {
                if (text.length == 1)
                    listEditText[1].requestFocus()

                setLoginButtonMode(isAllOtpBoxesFilled(), false)
            }

            R.id.etOtp2 -> {
                if (text.length == 1)
                    listEditText[2].requestFocus() else if (text.isBlank()) listEditText[0].requestFocus()

                setLoginButtonMode(isAllOtpBoxesFilled(), false)
            }

            R.id.etOtp3 -> {
                if (text.length == 1)
                    listEditText[3].requestFocus() else if (text.isBlank()) listEditText[1].requestFocus()

                setLoginButtonMode(isAllOtpBoxesFilled(), false)
            }

            R.id.etOtp4 -> {
                if (text.isBlank())
                    listEditText[2].requestFocus()

                setLoginButtonMode(isAllOtpBoxesFilled(), true)
            }
        }
    }

    private fun isAllOtpBoxesFilled() : Boolean {
        var flag = true
        listEditText.forEach {
            if (it.getTrimText().isBlank())
                flag = false
        }
        return flag
    }

}