package com.skilrock.boomlotto.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.WinnerListIgeAdapter
import com.skilrock.boomlotto.databinding.FragmentInstantGamesWinnerBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.response.WinnerListResponse
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.utility.WEAVER
import com.skilrock.boomlotto.viewmodels.WinnerListViewModel

class InstantGamesWinnerListFragment : BaseFragment() {

    private lateinit var binding: FragmentInstantGamesWinnerBinding
    private lateinit var viewModel: WinnerListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_instant_games_winner, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        viewModel.callWinnerListApi()
        setListeners()
    }

    private fun setListeners() {
        binding.pullToRefresh.setOnRefreshListener {
            binding.rvDrawTickets.visibility    = View.GONE
            binding.animationView.visibility    = View.VISIBLE
            binding.llNoData.visibility         = View.GONE
            viewModel.callWinnerListApi()
            binding.pullToRefresh.isRefreshing  = false
        }
        binding.pullToRefresh.setColorSchemeResources(R.color.color_app_pink)
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[WinnerListViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDateWinnerList.observe(master, observerWinnerList)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private val observerWinnerList = Observer<ResponseStatus<WinnerListResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val igeList = it.response.data?.iGE

                if (igeList == null || igeList.isEmpty()) {
                    binding.rvDrawTickets.visibility    = View.GONE
                    binding.animationView.visibility    = View.GONE
                    binding.llNoData.visibility         = View.VISIBLE
                } else {
                    val adapterIge = WinnerListIgeAdapter(master, igeList)
                    binding.rvDrawTickets.apply {
                        layoutManager = LinearLayoutManager(master)
                        setHasFixedSize(true)
                        adapter = adapterIge
                    }
                    binding.animationView.visibility    = View.GONE
                    binding.rvDrawTickets.visibility    = View.VISIBLE
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, WEAVER, it.errorCode)
                ErrorSheet(getString(R.string.boom_lotto_error), errorMessage, master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.boom_lotto_error), getString(it.errorMessageCode), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {

    }
}