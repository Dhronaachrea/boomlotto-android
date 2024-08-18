package com.skilrock.boomlotto.dialogs

import android.annotation.SuppressLint
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
import com.skilrock.boomlotto.databinding.SheetDepositSuccessBinding
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.SharedPrefUtils
import java.text.SimpleDateFormat


class DepositSuccessSheet( private val isBonusReceived: Boolean, private val depositAmount: String, private val bonusAmount: String,
                          private val transactionId: String, private val transactionTime: String,
                          private val onCloseClick:()->Unit, private val onClickPlayBoomFive:()->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetDepositSuccessBinding

    companion object {
        const val TAG = "DepositSuccessSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_deposit_success, container, false)
        setSheetExpanded()
        isCancelable = false
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

        setTitles()
        setBalanceBannerInfo()
        setListeners()
    }

    private fun setTitles() {
        binding.tvHeading.text          = if (isBonusReceived) getString(R.string.surprise_you_have_got_bonus) else getString(R.string.deposit_successful)
        binding.tvSubHeading.text       = if (isBonusReceived) getString(R.string.you_have_successfully_deposited_and_earned_yourself_bonus) else getString(R.string.you_have_successfully_deposited)
        binding.tvDepositAmount.text    = (PlayerInfo.getCurrency() + " " + depositAmount)

        binding.tvTransactionId.text    = transactionId
        binding.tvTransactionTime.text  = getTime(transactionTime)

        if (isBonusReceived) {
            binding.tvBonusDescription.visibility   = View.VISIBLE
            binding.tvBonusDescription.text         = (PlayerInfo.getCurrency() + " " + bonusAmount + " (" + getString(R.string.bonus) + ") " + getString(R.string.on_the) + " " + PlayerInfo.getCurrency() + " " + depositAmount + " " + getString(R.string.txn_amt))
        } else
            binding.tvBonusDescription.visibility   = View.GONE

        context?.let { cxt ->
            val unitPrice = SharedPrefUtils.getBoomUnitPrice(cxt)
            if (unitPrice.isNotBlank()) {
                binding.cardBoomUnitPrice.visibility    = View.VISIBLE
                binding.tvBoomUnitPrice.text            = unitPrice
            } else
                binding.cardBoomUnitPrice.visibility = View.INVISIBLE
        } ?: run {
            binding.cardBoomUnitPrice.visibility = View.INVISIBLE
        }
    }

    private fun setBalanceBannerInfo() {
        binding.tvBalance.text              = PlayerInfo.getPlayerTotalBalance()
        binding.tvWithdrawAbleBalance.text  = HtmlCompat.fromHtml(getString(R.string.withdrawable_amount) + " <b>" +  PlayerInfo.getPlayerWithdrawalAbleBalance() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.cardBalance.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction {
            binding.cardBalance.visibility = View.VISIBLE
            binding.cardBalance.animate().scaleX(1f).scaleY(1f).setDuration(600).withEndAction { }
        }
    }

    private fun setListeners() {
        binding.llClose.setOnClickListener {
            dismiss()
            onCloseClick()
        }

        binding.cardBoomUnitPrice.setOnClickListener {
            dismiss()
            onClickPlayBoomFive()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(transactionDate: String): String {
        return try {
            //"2021-11-13 11:52:59"
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter = SimpleDateFormat("MMM d, yyyy hh:mm aa")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: transactionDate
        } catch (e: Exception) {
            transactionDate
        }
    }

}