package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.SheetBoomTicketBuyBinding
import com.skilrock.boomlotto.utility.getFormattedAmount
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BoomTicketBuySheet(private val title1: String, private val title2: String, private val amount: String,
                         private val ticketNumber: String, private val url: String, private val onCloseClick:()->Unit,
                         private val onPlayAgainClick:()->Unit, private val onPlayInstantGamesClick:()->Unit, private val onTicketListClick:()->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetBoomTicketBuyBinding

    companion object {
        const val TAG = "BoomTicketBuySheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_boom_ticket_buy, container, false)
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

        animateImage()
        setDonationImage()
        setTitles()
        setListeners()
    }

    private fun setDonationImage() {
        if (url.isBlank())
            binding.ivDonation.visibility = View.GONE
        else {
            binding.ivDonation.visibility = View.VISIBLE
            Glide.with(binding.ivDonation.context).load(url).into(binding.ivDonation)
        }
    }

    private fun setTitles() {
        binding.tvDonationTitle1.text   = title1
        binding.tvDonationTitle2.text   = title2
        binding.tvTicketNumber.text     = ticketNumber
        binding.tvTotal.text            = amount
    }

    private fun animateImage() {
        binding.ivLogo.animate().setDuration(1).scaleX(0f).scaleY(0f)
        binding.ivLogo.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            delay(400)
            binding.ivLogo.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(400)
                .withEndAction {}
        }
    }

    private fun setListeners() {
        binding.llClose.setOnClickListener {
            dismiss()
            onCloseClick()
        }

        binding.tvShare.setOnClickListener {

        }

        binding.tvCheckTicketsClick.setOnClickListener {
            dismiss()
            onTicketListClick()
        }

        binding.tvPlayInstantGames.setOnClickListener {
            dismiss()
            onPlayInstantGamesClick()
        }

        binding.btnPlayAgain.setOnClickListener {
            dismiss()
            onPlayAgainClick()
        }
    }

}