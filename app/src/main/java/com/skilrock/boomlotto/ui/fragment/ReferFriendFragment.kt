package com.skilrock.boomlotto.ui.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentReferFriendBinding
import com.skilrock.boomlotto.utility.PlayerInfo.getReferFriendCode

class ReferFriendFragment : BaseFragment() {

    private lateinit var binding: FragmentReferFriendBinding
    private var copiedTextAtClip = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_refer_friend, container, false)
        return binding.root
    }

    companion object {
        const val TAG = "ReferFriendFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViews()
        setAnimationForReferCoupon()
        setOnClickListener()
    }

    private fun setViews() {
        binding.tvReferFriendCode.text  = getReferFriendCode()
        val shareMessage                = getString(R.string.share_your_code_with_your_friends) + "<br/>" + getString(R.string.and_get_a_bonus_of_aed) + " <b>50</b>"
        binding.tvShareMessage.text     = HtmlCompat.fromHtml(shareMessage, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setOnClickListener() {
        binding.flTapToCopy.setOnClickListener {
            binding.tvTapToCopy.text            = getString(R.string.copied)
            copiedTextAtClip                    = getReferFriendCode()
            binding.animationView.visibility    = View.VISIBLE
            setAnimationAfterCopy()
            setCopiedTextToClipBoard()
        }

        binding.btnShare.setOnClickListener {
            val intent      = Intent()
            intent.action   = Intent.ACTION_SEND
            intent.type     = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getReferFriendCode())
            startActivity(Intent.createChooser(intent, "Share via"))
        }
    }

    private fun setAnimationForReferCoupon() {
        binding.flTapToCopy.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction {
            binding.flTapToCopy.visibility = View.VISIBLE
            binding.flTapToCopy.animate().scaleX(1f).scaleY(1f).setDuration(600).withEndAction { }
        }
    }

    private fun setAnimationAfterCopy() {
        binding.tvTapToCopy.animate().alpha(.1F).setDuration(200).withEndAction {
            binding.tvTapToCopy.animate().alpha(1F).duration = 200
        }
        binding.tvReferFriendCode.animate().scaleX(1.4F).scaleY(1.4F).setDuration(200).withEndAction {
            binding.tvReferFriendCode.animate().scaleX(1F).scaleY(1F).duration = 200
        }
    }

    private fun setCopiedTextToClipBoard() {
        val clipboard = master.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Refer Code", getReferFriendCode())
        clipboard.setPrimaryClip(clip)
    }

    override fun hideKeyboard() {

    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.refer_a_friend), View.VISIBLE, View.GONE)
    }
}