package com.skilrock.boomlotto.ui.fragment

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.R.color
import com.skilrock.boomlotto.R.layout
import com.skilrock.boomlotto.adapters.WithdrawalViewPagerAdapter
import com.skilrock.boomlotto.databinding.FragmentWithdrawalViewPagerBinding
import kotlinx.android.synthetic.main.fragment_my_tickets.*

class WithdrawalViewPagerFragment : BaseFragment() {

    private lateinit var binding: FragmentWithdrawalViewPagerBinding
    private lateinit var viewPagerAdapter: WithdrawalViewPagerAdapter
    private var mSelectedTabIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, layout.fragment_withdrawal_view_pager, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements()
        setViewModel()
        setViewPagerAndTabs()
        setPageChangeListener()
        setClickListeners()
    }

    private fun setViewModel() {
        binding.lifecycleOwner = this
    }

    private fun setViewPagerAndTabs() {
        viewPagerAdapter            = WithdrawalViewPagerAdapter(master)
        viewPagerTickets.adapter    = viewPagerAdapter
    }

    private fun setPageChangeListener() {
        binding.viewPagerTickets.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mSelectedTabIndex = position
                animateTab(position)
            }
        })
    }

    private fun animateTab(position: Int) {
        val selectedColor   = ContextCompat.getColor(master, color.my_ticket_tab_selected)
        val unSelectedColor = ContextCompat.getColor(master, color.tab_text_unselected)

        if (position == 0) {
            val colorAnimationSelection = ValueAnimator.ofObject(ArgbEvaluator(), unSelectedColor, selectedColor)
            colorAnimationSelection.duration = 400

            colorAnimationSelection.addUpdateListener { animator ->
                binding.tvTabNew.setTextColor(animator.animatedValue as Int)
                binding.tvTabNewBar.setBackgroundColor(animator.animatedValue as Int)
                val params = binding.tvTabNewBar.layoutParams
                params.height = resources.getDimension(R.dimen.tab_bar_selected_height).toInt()
                binding.tvTabNewBar.layoutParams = params
            }
            colorAnimationSelection.start()

            val colorAnimationDeselection = ValueAnimator.ofObject(ArgbEvaluator(), selectedColor, unSelectedColor)
            colorAnimationDeselection.duration = 400

            colorAnimationDeselection.addUpdateListener { animator ->
                binding.tvTabPending.setTextColor(animator.animatedValue as Int)
                binding.tvTabPendingBar.setBackgroundColor(animator.animatedValue as Int)
                val params = binding.tvTabPendingBar.layoutParams
                params.height = resources.getDimension(R.dimen.tab_bar_unselected_height).toInt()
                binding.tvTabPendingBar.layoutParams = params
            }
            colorAnimationDeselection.start()
        } else {
            val colorAnimationSelection = ValueAnimator.ofObject(ArgbEvaluator(), unSelectedColor, selectedColor)
            colorAnimationSelection.duration = 400

            colorAnimationSelection.addUpdateListener { animator ->
                binding.tvTabPending.setTextColor(animator.animatedValue as Int)
                binding.tvTabPendingBar.setBackgroundColor(animator.animatedValue as Int)
                val params = binding.tvTabPendingBar.layoutParams
                params.height = resources.getDimension(R.dimen.tab_bar_selected_height).toInt()
                binding.tvTabPendingBar.layoutParams = params
            }
            colorAnimationSelection.start()

            val colorAnimationDeselection = ValueAnimator.ofObject(ArgbEvaluator(), selectedColor, unSelectedColor)
            colorAnimationDeselection.duration = 400

            colorAnimationDeselection.addUpdateListener { animator ->
                binding.tvTabNew.setTextColor(animator.animatedValue as Int)
                binding.tvTabNewBar.setBackgroundColor(animator.animatedValue as Int)
                val params = binding.tvTabNewBar.layoutParams
                params.height = resources.getDimension(R.dimen.tab_bar_unselected_height).toInt()
                binding.tvTabNewBar.layoutParams = params
            }
            colorAnimationDeselection.start()
        }
    }

    private fun setClickListeners() {
        binding.tvTabNew.setOnClickListener {
            if (mSelectedTabIndex != 0) {
                binding.viewPagerTickets.setCurrentItem(0, true)
                animateTab(0)
            }
        }

        binding.tvTabPending.setOnClickListener {
            if (mSelectedTabIndex != 1) {
                binding.viewPagerTickets.setCurrentItem(1, true)
                animateTab(1)
            }
        }
    }

    override fun hideKeyboard() {}

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.withdrawal), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }
}