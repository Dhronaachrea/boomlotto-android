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
import com.skilrock.boomlotto.databinding.SheetRegistrationBinding

class RegistrationSheet(private val bonusAmount: Double, private val currencyCode: String,
                        private val onAddCashClick:()->Unit, private val onContinueClick:()->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetRegistrationBinding

    companion object {
        const val TAG = "RegistrationSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_registration, container, false)
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

        isCancelable = false
        if (bonusAmount > 0) {
            binding.tvHeader.text               = getString(R.string.mabrook_exclamation)
            binding.tvMessage.text              = getString(R.string.referral_bonus_message)
            binding.tvBonusAmount.text          = ("$currencyCode $bonusAmount")
            binding.clBonusAmount.visibility    = View.VISIBLE
            binding.ivHeaderIcon.setImageResource(R.drawable.icon_referral_one)
        } else {
            binding.tvHeader.text               = getString(R.string.you_are_all_set)
            binding.tvMessage.text              = getString(R.string.successful_registration)
            binding.clBonusAmount.visibility    = View.GONE
            binding.ivHeaderIcon.setImageResource(R.drawable.icon_registered)
        }

        binding.btnAddCash.setOnClickListener {
            onAddCashClick()
            dismiss()
        }

        binding.tvContinue.setOnClickListener {
            onContinueClick()
            dismiss()
        }

        binding.btnCross.setOnClickListener {
            onContinueClick()
            dismiss()
        }
    }

}