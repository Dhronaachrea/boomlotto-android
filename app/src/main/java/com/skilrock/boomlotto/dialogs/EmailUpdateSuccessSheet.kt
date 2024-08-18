package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.SheetEmailUpdateSuccessBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class EmailUpdateSuccessSheet(private val successMessage: String, private val onCloseClick:()->Unit) : BottomSheetDialogFragment(){

    private lateinit var bottomsheetbinding: SheetEmailUpdateSuccessBinding

    companion object {
        const val TAG = "AddBankSuccessSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = com.skilrock.boomlotto.R.style.SheetAnimation
        bottomsheetbinding = DataBindingUtil.inflate(inflater, com.skilrock.boomlotto.R.layout.sheet_email_update_success, container, false)
        setSheetExpanded()
        return bottomsheetbinding.root
    }

    private fun setSheetExpanded() {
        dialog?.setOnShowListener { dialog ->
            val sheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal: View? = sheetDialog.findViewById(R.id.design_bottom_sheet)
            bottomSheetInternal?.let { BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animateImage()
        onClickListener()
        setSuccessMessage()
    }

    private fun setSuccessMessage() {
        bottomsheetbinding.tvSuccessMessage.text = HtmlCompat.fromHtml(successMessage, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun onClickListener(){
        bottomsheetbinding.btnOkay.setOnClickListener {
            onCloseClick()
            dismiss()
        }
   }

    private fun animateImage() {
        bottomsheetbinding.ivLogo.animate().setDuration(1).scaleX(0f).scaleY(0f)
        bottomsheetbinding.ivLogo.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            delay(400)
            bottomsheetbinding.ivLogo.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(600)
                .withEndAction {}
        }
    }
}
