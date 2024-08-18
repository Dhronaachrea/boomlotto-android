package com.skilrock.boomlotto.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentWithdrawLowBalanceBinding
import com.skilrock.boomlotto.utility.PlayerInfo

class WithdrawLowBalanceFragment : BaseFragment() {

    private lateinit var binding: FragmentWithdrawLowBalanceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdraw_low_balance, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        setValues()
    }

    private fun setViewModel() {
        binding.lifecycleOwner = this
    }

    private fun setValues() {
        binding.tvBalance.text          = PlayerInfo.getPlayerTotalBalance()
        binding.tvWithdrawalAmount.text = HtmlCompat.fromHtml(getString(R.string.amount_is) + " <b>" +  PlayerInfo.getPlayerWithdrawalAbleBalance() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        val animation = TranslateAnimation(0.0f, 0.0f, 60.0f, 0.0f)
        animation.duration      = 500
        animation.repeatCount   = 0
        animation.fillAfter     = false

        /*val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() //add this
        fadeIn.duration = 500*/

        binding.llBox.visibility = View.VISIBLE
        binding.llBox.startAnimation(animation)
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.withdrawal), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }
}