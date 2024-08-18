package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.SheetAppUpdateBinding

class AppUpdateSheet(private val text: String,
                     private val onButtonPress:()->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetAppUpdateBinding

    companion object {
        const val TAG = "AppUpdateSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_app_update, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable            = false
        binding.tvMessage.text  = text

        binding.btnClose.setOnClickListener {
            onButtonPress()
            dismiss()
        }
    }

}