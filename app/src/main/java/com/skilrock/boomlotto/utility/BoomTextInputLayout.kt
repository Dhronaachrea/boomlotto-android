package com.skilrock.boomlotto.utility

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class BoomTextInputLayout : TextInputLayout {

    private var textInputEditText: TextInputEditText? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (textInputEditText == null) {
            val rootView: View? = getChildAt(0)
            if (rootView is FrameLayout) {
                val frameLayout: FrameLayout = rootView
                for (index in 0 until frameLayout.childCount) {
                    val nestedView: View? = frameLayout.getChildAt(index)
                    if (nestedView is TextInputEditText) {
                        textInputEditText = nestedView
                        textInputEditText?.let { clearErrorOnTextChange(it) }
                    }
                }
            }
        }
    }

    override fun setError(error: CharSequence?) {
        super.setError(error)
        val layout: View? = getChildAt(1)
        layout?.let { child ->
            if (error != null && "" != error.toString().trim { it <= ' ' })
                child.visibility = VISIBLE
            else
                child.visibility = GONE
        }
    }

    private fun clearErrorOnTextChange(textInputEditText: TextInputEditText) {
        textInputEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                /*Gets called after text change*/
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /*Gets called before text change*/
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = null
                if (textInputEditText.getTrimText().isBlank()) {
                    if (s.toString() == " ") {
                        val result: String = textInputEditText.getTrimText()
                        textInputEditText.setText(result)
                        textInputEditText.setSelection(result.length)
                    }
                }
                else if (textInputEditText.text.toString().takeLast(2) == "  ") {
                    val result: String = textInputEditText.text.toString().dropLast(1)
                    textInputEditText.setText(result)
                    textInputEditText.setSelection(result.length)
                }
            }
        })
    }

}