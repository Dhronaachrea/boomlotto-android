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
import com.skilrock.boomlotto.adapters.WithdrawalPendingAdapter
import com.skilrock.boomlotto.databinding.FragmentWithdrawalPendingTransactionsBinding
import com.skilrock.boomlotto.models.response.WithdrawalPendingResponse
import com.skilrock.boomlotto.utility.CASHIER
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.viewmodels.WithdrawalPendingTransactionsViewModel

class WithdrawalPendingTransactionsFragment : BaseFragment() {

    private lateinit var binding: FragmentWithdrawalPendingTransactionsBinding
    private lateinit var viewModel: WithdrawalPendingTransactionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdrawal_pending_transactions, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        callWithdrawalPendingApi()
        setListeners()
    }

    private fun setListeners() {
        binding.pullToRefresh.setOnRefreshListener {
            binding.rvPendingWithdraw.visibility    = View.GONE
            binding.animationView.visibility        = View.VISIBLE
            binding.llNoData.visibility             = View.GONE
            callWithdrawalPendingApi()
            binding.pullToRefresh.isRefreshing  = false
        }
        binding.pullToRefresh.setColorSchemeResources(R.color.color_app_pink)
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[WithdrawalPendingTransactionsViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataPendingResponse.observe(viewLifecycleOwner, observerWithdrawalPendingResponse)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun callWithdrawalPendingApi() {
        viewModel.callWithdrawalPendingApi()
    }

    private val observerWithdrawalPendingResponse = Observer<ResponseStatus<WithdrawalPendingResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val response: WithdrawalPendingResponse = it.response
                response.txnList?.let { list ->

                    if (list.size > 0) {
                        binding.rvPendingWithdraw.apply {
                            layoutManager = LinearLayoutManager(master)
                            adapter = WithdrawalPendingAdapter(master, list)
                        }
                        binding.rvPendingWithdraw.visibility    = View.VISIBLE
                        binding.animationView.visibility        = View.GONE
                        binding.llNoData.visibility             = View.GONE
                        binding.rvPendingWithdraw.scheduleLayoutAnimation()
                    } else {
                        binding.rvPendingWithdraw.visibility    = View.GONE
                        binding.animationView.visibility        = View.GONE
                        binding.llNoData.visibility             = View.VISIBLE
                        binding.tvNoDataTitle.text              = getString(R.string.no_pending_withdrawal)
                        binding.tvNoDataText.text               = getString(R.string.it_seems_you_don_t_have_any_withdrawal_which_is_pending)
                    }
                } ?: run {
                    binding.rvPendingWithdraw.visibility    = View.GONE
                    binding.animationView.visibility        = View.GONE
                    binding.llNoData.visibility             = View.VISIBLE
                    binding.tvNoDataTitle.text              = getString(R.string.no_pending_withdrawal)
                    binding.tvNoDataText.text               = getString(R.string.it_seems_you_don_t_have_any_withdrawal_which_is_pending)
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage                        = master.getResponseMessage(master, CASHIER, it.errorCode)
                binding.rvPendingWithdraw.visibility    = View.GONE
                binding.animationView.visibility        = View.GONE
                binding.llNoData.visibility             = View.VISIBLE
                binding.tvNoDataTitle.text              = getString(R.string.oops)
                binding.tvNoDataText.text               = errorMessage
            }

            is ResponseStatus.TechnicalError -> {
                val errorMessage                        = getString(it.errorMessageCode)
                binding.rvPendingWithdraw.visibility    = View.GONE
                binding.animationView.visibility        = View.GONE
                binding.llNoData.visibility             = View.VISIBLE
                binding.tvNoDataTitle.text              = getString(R.string.oops)
                binding.tvNoDataText.text               = errorMessage
            }
        }
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {

    }
}