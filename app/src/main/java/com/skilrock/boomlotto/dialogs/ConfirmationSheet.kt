package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.SheetConfirmationBinding

class ConfirmationSheet(private val title: String, private val text: String, private val buttonCancelText: String, private val buttonOkayText: String,
                        private val onButtonCancelPress:()->Unit, private val onButtonOkayPress:()->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetConfirmationBinding

    companion object {
        const val TAG = "ConfirmationSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_confirmation, container, false)
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
        binding.tvHeader.text   = title
        binding.tvMessage.text  = text
        binding.btnCancel.text  = buttonCancelText
        binding.btnOkay.text    = buttonOkayText

        binding.btnCancel.setOnClickListener {
            onButtonCancelPress()
            dismiss()
        }

        binding.btnOkay.setOnClickListener {
            onButtonOkayPress()
            dismiss()
        }
    }

}