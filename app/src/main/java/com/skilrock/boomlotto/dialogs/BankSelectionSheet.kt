package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.BankAdapter
import com.skilrock.boomlotto.databinding.SheetBankSelectionBinding
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BankSelectionSheet(private val mPayTypeMapList: List<PaymentOptionsResponse.PayTypeMap>,
                         private val onBankSelected:(PaymentOptionsResponse.PayTypeMap)->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetBankSelectionBinding

    companion object {
        const val TAG = "BankSelectionSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_bank_selection, container, false)
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
        isCancelable = true

        val bankAdapter = BankAdapter(::onBankClick)
        binding.rvBank.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = bankAdapter
        }
        bankAdapter.setBankList(mPayTypeMapList)

        lifecycleScope.launch {
            delay(300)
            binding.rvBank.visibility = View.VISIBLE
        }
    }

    private fun onBankClick(bank: PaymentOptionsResponse.PayTypeMap) {
        onBankSelected(bank)
        dismiss()
    }

}