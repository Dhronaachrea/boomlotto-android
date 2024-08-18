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
import com.skilrock.boomlotto.databinding.SheetDepositFailedBinding
import com.skilrock.boomlotto.utility.PlayerInfo

class DepositTransactionFailedSheet(private val message: String, private val depositAmount: String) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetDepositFailedBinding

    companion object {
        const val TAG = "DepositTransactionFailedSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_deposit_failed, container, false)
        isCancelable = false
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

        binding.tvDepositAmount.text    = (PlayerInfo.getCurrency() + " " + depositAmount)
        binding.tvMessage.text          = if (message.isBlank()) getString(R.string.your_transaction_failed) else message

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

}