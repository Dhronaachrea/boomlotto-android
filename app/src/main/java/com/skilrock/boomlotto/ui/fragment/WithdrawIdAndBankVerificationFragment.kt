package com.skilrock.boomlotto.ui.fragment

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.R.color
import com.skilrock.boomlotto.R.layout
import com.skilrock.boomlotto.adapters.WithdrawVerificationViewPagerAdapter
import com.skilrock.boomlotto.databinding.FragmentWithdrawIdAndBankVerificationBinding
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WithdrawIdAndBankVerificationFragment : BaseFragment() {

    private lateinit var binding: FragmentWithdrawIdAndBankVerificationBinding
    private lateinit var viewPagerAdapter: WithdrawVerificationViewPagerAdapter
    private var mSelectedTabIndex = 0
    private var isIdVerified = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, layout.fragment_withdraw_id_and_bank_verification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements()
        setViewModel()
        setViewPagerAndTabs()
        setPageChangeListener()
        setPageIndex()
    }

    private fun setViewModel() {
        binding.lifecycleOwner = this

        this.arguments?.let {
            val amount      = it.getString("amount", "")
            isIdVerified    = it.getBoolean("isIdVerified", false)
            val text        = getString(R.string.first_time_withdrawal_request) + ": <b>" + PlayerInfo.getCurrency() + " " + amount + "</b>"
            binding.tvFirstTimeRequestAmount.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } ?: run {
            master.showToast(getString(R.string.some_problem_occurred))
            master.supportFragmentManager.popBackStack()
        }
    }

    private fun setViewPagerAndTabs() {
        viewPagerAdapter            = WithdrawVerificationViewPagerAdapter(master, ::shiftToBankVerification)
        binding.viewPager.adapter   = viewPagerAdapter
        binding.viewPager.isUserInputEnabled = false
    }

    private fun setPageIndex() {
        if (isIdVerified) {
            lifecycleScope.launch {
                delay(400)
                binding.viewPager.setCurrentItem(1, true)
            }
        }
    }

    private fun setPageChangeListener() {
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mSelectedTabIndex = position
                animateTab(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun animateTab(position: Int) {
        val mSelectedTabColor       = ContextCompat.getColor(master, color.color_app_purple)
        val mUnSelectedTabColor     = ContextCompat.getColor(master, color.purple_light)

        val mSelectedTabTextColor   = ContextCompat.getColor(master, color.color_app_purple)
        val mUnSelectedTabTextColor = ContextCompat.getColor(master, color.color_disable_text)

        val mSelectedTextColor   = ContextCompat.getColor(master, color.white)
        val mUnSelectedTextColor = ContextCompat.getColor(master, color.color_app_purple)
        if (position == 0) {
            val colorAnimationBackgroundEnabled = ValueAnimator.ofObject(ArgbEvaluator(), mUnSelectedTabColor, mSelectedTabColor)
            colorAnimationBackgroundEnabled.duration = 400

            colorAnimationBackgroundEnabled.addUpdateListener { animator ->
                binding.card01.setCardBackgroundColor(animator.animatedValue as Int)
                binding.line00.setBackgroundColor(animator.animatedValue as Int)
                binding.line01.setBackgroundColor(animator.animatedValue as Int)
            }
            colorAnimationBackgroundEnabled.start()

            val colorAnimationBackgroundDisabled = ValueAnimator.ofObject(ArgbEvaluator(), mSelectedTabColor, mUnSelectedTabColor)
            colorAnimationBackgroundDisabled.duration = 400

            colorAnimationBackgroundDisabled.addUpdateListener { animator ->
                binding.card02.setCardBackgroundColor(animator.animatedValue as Int)
                binding.line10.setBackgroundColor(animator.animatedValue as Int)
                binding.line11.setBackgroundColor(animator.animatedValue as Int)
            }
            colorAnimationBackgroundDisabled.start()

            val colorAnimationTextEnabled = ValueAnimator.ofObject(ArgbEvaluator(), mUnSelectedTabTextColor, mSelectedTabTextColor)
            colorAnimationTextEnabled.duration = 400

            colorAnimationTextEnabled.addUpdateListener { animator ->
                binding.tvIdVerification.setTextColor(animator.animatedValue as Int)
            }
            colorAnimationTextEnabled.start()

            val colorAnimationTextDisabled = ValueAnimator.ofObject(ArgbEvaluator(), mSelectedTabTextColor, mUnSelectedTabTextColor)
            colorAnimationTextDisabled.duration = 400

            colorAnimationTextDisabled.addUpdateListener { animator ->
                binding.tvBankDetails.setTextColor(animator.animatedValue as Int)
            }
            colorAnimationTextDisabled.start()

            val colorAnimationTextCircleEnabled = ValueAnimator.ofObject(ArgbEvaluator(), mUnSelectedTextColor, mSelectedTextColor)
            colorAnimationTextCircleEnabled.duration = 400

            colorAnimationTextCircleEnabled.addUpdateListener { animator ->
                binding.tv01.setTextColor(animator.animatedValue as Int)
            }
            colorAnimationTextCircleEnabled.start()

            val colorAnimationTextCircleDisabled = ValueAnimator.ofObject(ArgbEvaluator(), mSelectedTextColor, mUnSelectedTextColor)
            colorAnimationTextCircleDisabled.duration = 400

            colorAnimationTextCircleDisabled.addUpdateListener { animator ->
                binding.tv02.setTextColor(animator.animatedValue as Int)
            }
            colorAnimationTextCircleDisabled.start()
        } else {
            val colorAnimationBackgroundEnabled = ValueAnimator.ofObject(ArgbEvaluator(), mUnSelectedTabColor, mSelectedTabColor)
            colorAnimationBackgroundEnabled.duration = 400

            colorAnimationBackgroundEnabled.addUpdateListener { animator ->
                binding.card02.setCardBackgroundColor(animator.animatedValue as Int)
                binding.line10.setBackgroundColor(animator.animatedValue as Int)
                binding.line11.setBackgroundColor(animator.animatedValue as Int)
            }
            colorAnimationBackgroundEnabled.start()

            val colorAnimationBackgroundDisabled = ValueAnimator.ofObject(ArgbEvaluator(), mSelectedTabColor, mUnSelectedTabColor)
            colorAnimationBackgroundDisabled.duration = 400

            colorAnimationBackgroundDisabled.addUpdateListener { animator ->
                binding.card01.setCardBackgroundColor(animator.animatedValue as Int)
                binding.line00.setBackgroundColor(animator.animatedValue as Int)
                binding.line01.setBackgroundColor(animator.animatedValue as Int)
            }
            colorAnimationBackgroundDisabled.start()

            val colorAnimationTextEnabled = ValueAnimator.ofObject(ArgbEvaluator(), mUnSelectedTabTextColor, mSelectedTabTextColor)
            colorAnimationTextEnabled.duration = 400

            colorAnimationTextEnabled.addUpdateListener { animator ->
                binding.tvBankDetails.setTextColor(animator.animatedValue as Int)
            }
            colorAnimationTextEnabled.start()

            val colorAnimationTextDisabled = ValueAnimator.ofObject(ArgbEvaluator(), mSelectedTabTextColor, mUnSelectedTabTextColor)
            colorAnimationTextDisabled.duration = 400

            colorAnimationTextDisabled.addUpdateListener { animator ->
                binding.tvIdVerification.setTextColor(animator.animatedValue as Int)
            }
            colorAnimationTextDisabled.start()

            val colorAnimationTextCircleEnabled = ValueAnimator.ofObject(ArgbEvaluator(), mUnSelectedTextColor, mSelectedTextColor)
            colorAnimationTextCircleEnabled.duration = 400

            colorAnimationTextCircleEnabled.addUpdateListener { animator ->
                binding.tv02.setTextColor(animator.animatedValue as Int)
            }
            colorAnimationTextCircleEnabled.start()

            val colorAnimationTextCircleDisabled = ValueAnimator.ofObject(ArgbEvaluator(), mSelectedTextColor, mUnSelectedTextColor)
            colorAnimationTextCircleDisabled.duration = 400

            colorAnimationTextCircleDisabled.addUpdateListener { animator ->
                binding.tv01.setTextColor(animator.animatedValue as Int)
            }
            colorAnimationTextCircleDisabled.start()
        }
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

    private fun shiftToBankVerification() {
        binding.viewPager.setCurrentItem(1, true)
        binding.nestedScrollView.scrollTo(0, 0)
    }
}