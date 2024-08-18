package com.skilrock.boomlotto.dialogs

import android.annotation.SuppressLint
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
import com.skilrock.boomlotto.databinding.SheetWithdrawalRequestRaisedBinding
import com.skilrock.boomlotto.utility.PlayerInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class WithdrawalRequestRaiseSheet(private val debitedAmount: String, private val transactionId: String, private val transactionDate: String, private val onCloseClick:()->Unit) : BottomSheetDialogFragment(){

    private lateinit var bottomsheetbinding: SheetWithdrawalRequestRaisedBinding

    companion object {
        const val TAG = "WithdrawalRequestRaiseSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        bottomsheetbinding = DataBindingUtil.inflate(inflater, R.layout.sheet_withdrawal_request_raised, container, false)
        setSheetExpanded()
        isCancelable = false
        return bottomsheetbinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animateImage()
        setDataInThisBottomSheet()
        setClickListeners()
    }

    private fun setSheetExpanded() {
        dialog?.setOnShowListener { dialog ->
            val sheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal: View? = sheetDialog.findViewById(R.id.design_bottom_sheet)
            bottomSheetInternal?.let { BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED }
        }
    }

    private fun setDataInThisBottomSheet() {
        bottomsheetbinding.tvBalance.text                   = HtmlCompat.fromHtml(PlayerInfo.getPlayerTotalBalanceBold(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        bottomsheetbinding.tvDebitedAmountFromWallet.text   = (PlayerInfo.getCurrency() + " " + debitedAmount)
        bottomsheetbinding.tvTransactionId.text             = transactionId
        bottomsheetbinding.tvTransactionTime.text           = getDateToShow(transactionDate)
    }

    private fun setClickListeners(){
        bottomsheetbinding.btnCross.setOnClickListener {
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

    @SuppressLint("SimpleDateFormat")
    private fun getDateToShow(transactionDate: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val formatter = SimpleDateFormat("MMM dd, yyyy, hh:mm aa")
            parser.parse(transactionDate)?.let { formatter.format(it) }
                ?: transactionDate
        } catch (e: Exception) {
            transactionDate
        }
    }
}
