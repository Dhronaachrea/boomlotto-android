package com.skilrock.boomlotto.ui.fragment

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.R.color
import com.skilrock.boomlotto.R.layout
import com.skilrock.boomlotto.adapters.WinnerViewPagerAdapter
import com.skilrock.boomlotto.databinding.FragmentWinnerBinding
import com.skilrock.boomlotto.models.request.AppAnalyticsRequest
import com.skilrock.boomlotto.models.response.AppAnalyticsResponse
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.utility.SharedPrefUtils
import com.skilrock.boomlotto.viewmodels.WinnerListViewModel
import kotlinx.android.synthetic.main.fragment_my_tickets.*

class WinnerFragment : BaseFragment() {

    private lateinit var binding: FragmentWinnerBinding
    private lateinit var viewModel: WinnerListViewModel
    private lateinit var viewPagerAdapter: WinnerViewPagerAdapter
    private var mSelectedTabIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, layout.fragment_winner, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements()
        setViewModel()
        setViewPagerAndTabs()
        setPageChangeListener()
        setClickListeners()
        callAnalyticsApi()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[WinnerListViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
        viewModel.liveDateAppAnalytics.observe(master, observerAppAnalytics)
    }

    private fun setViewPagerAndTabs() {
        viewPagerAdapter        = WinnerViewPagerAdapter(master)
        viewPagerTickets.adapter = viewPagerAdapter
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

    private fun callAnalyticsApi() {
        if (SharedPrefUtils.getString(master, SharedPrefUtils.PLAYER_TOKEN).isNotBlank()) {
            val hashMap = HashMap<String, String>()
            hashMap["playerId"]         = PlayerInfo.getPlayerId().toString()
            hashMap["playerBalance"]    = PlayerInfo.getPlayerTotalBalance()
            hashMap["playerUserName"]   = PlayerInfo.getPlayerUserName()

            viewModel.callAppAnalyticsApi(AppAnalyticsRequest(analyticsData = hashMap))
        }
    }

    private fun animateTab(position: Int) {
        val selectedColor   = ContextCompat.getColor(master, color.my_ticket_tab_selected)
        val unSelectedColor = ContextCompat.getColor(master, color.tab_text_unselected)

        if (position == 0) {
            val colorAnimationSelection = ValueAnimator.ofObject(ArgbEvaluator(), unSelectedColor, selectedColor)
            colorAnimationSelection.duration = 400

            colorAnimationSelection.addUpdateListener { animator ->
                binding.tvTabDrawGame.setTextColor(animator.animatedValue as Int)
                binding.tvTabDrawGameBar.setBackgroundColor(animator.animatedValue as Int)
                val params = binding.tvTabDrawGameBar.layoutParams
                params.height = resources.getDimension(R.dimen.tab_bar_selected_height).toInt()
                binding.tvTabDrawGameBar.layoutParams = params
            }
            colorAnimationSelection.start()

            val colorAnimationDeselection = ValueAnimator.ofObject(ArgbEvaluator(), selectedColor, unSelectedColor)
            colorAnimationDeselection.duration = 400

            colorAnimationDeselection.addUpdateListener { animator ->
                binding.tvTabInstantGame.setTextColor(animator.animatedValue as Int)
                binding.tvTabInstantGameBar.setBackgroundColor(animator.animatedValue as Int)
                val params = binding.tvTabInstantGameBar.layoutParams
                params.height = resources.getDimension(R.dimen.tab_bar_unselected_height).toInt()
                binding.tvTabInstantGameBar.layoutParams = params
            }
            colorAnimationDeselection.start()
        } else {
            val colorAnimationSelection = ValueAnimator.ofObject(ArgbEvaluator(), unSelectedColor, selectedColor)
            colorAnimationSelection.duration = 400

            colorAnimationSelection.addUpdateListener { animator ->
                binding.tvTabInstantGame.setTextColor(animator.animatedValue as Int)
                binding.tvTabInstantGameBar.setBackgroundColor(animator.animatedValue as Int)
                val params = binding.tvTabInstantGameBar.layoutParams
                params.height = resources.getDimension(R.dimen.tab_bar_selected_height).toInt()
                binding.tvTabInstantGameBar.layoutParams = params
            }
            colorAnimationSelection.start()

            val colorAnimationDeselection = ValueAnimator.ofObject(ArgbEvaluator(), selectedColor, unSelectedColor)
            colorAnimationDeselection.duration = 400

            colorAnimationDeselection.addUpdateListener { animator ->
                binding.tvTabDrawGame.setTextColor(animator.animatedValue as Int)
                binding.tvTabDrawGameBar.setBackgroundColor(animator.animatedValue as Int)
                val params = binding.tvTabDrawGameBar.layoutParams
                params.height = resources.getDimension(R.dimen.tab_bar_unselected_height).toInt()
                binding.tvTabDrawGameBar.layoutParams = params
            }
            colorAnimationDeselection.start()
        }
    }

    private fun setClickListeners() {
        binding.tvTabDrawGame.setOnClickListener {
            if (mSelectedTabIndex != 0) {
                binding.viewPagerTickets.setCurrentItem(0, true)
                animateTab(0)
            }
        }

        binding.tvTabInstantGame.setOnClickListener {
            if (mSelectedTabIndex != 1) {
                binding.viewPagerTickets.setCurrentItem(1, true)
                animateTab(1)
            }
        }
    }

    override fun hideKeyboard() {}

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.winner_list), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    val observerAppAnalytics = Observer<ResponseStatus<AppAnalyticsResponse>> {
        when (it) {
            is ResponseStatus.Success -> { }

            is ResponseStatus.Error -> { }

            is ResponseStatus.TechnicalError -> { }
        }
    }
}