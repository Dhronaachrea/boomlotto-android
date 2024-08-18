package com.skilrock.boomlotto.dialogs

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.SheetAddBankFilePreviewBinding

class AddBankPreviewSelectedFileSheet(private val imageUri: Uri, val path: String?) : BottomSheetDialogFragment() {

    private lateinit var binding : SheetAddBankFilePreviewBinding

    companion object {
        const val TAG = "AddBankPreviewSelectedFileSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_add_bank_file_preview, container, false)
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
        binding.cvNoImage.visibility    = View.GONE
        binding.cvImage.visibility      = View.VISIBLE
        binding.ivProfilePicShow.setImageURI(imageUri)
        path?.let {
            binding.selectedFileName.text = it.substringAfterLast("/")
        }
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.close.setOnClickListener {
            dismiss()
        }
    }
}