package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.SheetSessionExpiryBinding

class SessionExpirySheet(private val onCancelPress:()->Unit, private val onLoginPress:()->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetSessionExpiryBinding

    companion object {
        const val TAG = "SessionExpirySheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_session_expiry, container, false)
        setSheetExpanded()
        return binding.root
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

        isCancelable            = false
        binding.tvHeader.text   = getString(R.string.app_name)
        binding.tvMessage.text  = HtmlCompat.fromHtml(getString(R.string.your_session_has_expired), HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.btnCancel.setOnClickListener {
            dismiss()
            onCancelPress()
        }

        binding.btnLogin.setOnClickListener {
            onLoginPress()
            dismiss()
        }
    }

}