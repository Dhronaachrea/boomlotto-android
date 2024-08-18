package com.skilrock.boomlotto.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.AccountAdapter
import com.skilrock.boomlotto.databinding.FragmentWithdrawalBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.WithdrawalRequestRaiseSheet
import com.skilrock.boomlotto.models.request.WithdrawalRequest
import com.skilrock.boomlotto.models.response.HeaderInfoResponse
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse
import com.skilrock.boomlotto.models.response.WithdrawResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.WithdrawalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class WithdrawalFragment : BaseFragment() {

    private lateinit var binding: FragmentWithdrawalBinding
    private lateinit var viewModel: WithdrawalViewModel
    private lateinit var mSelectedPayTypeMap: PaymentOptionsResponse.PayTypeMap
    private lateinit var mWithdrawResponse: WithdrawResponse
    private var mSelectedPaymentAccId: Int = -1
    private var mMinimumWithdrawAmount = 0.0
    private var mMaximumWithdrawAmount = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdrawal, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        initializeWidgets()
        setListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[WithdrawalViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataPaymentOptions.observe(master, observerPaymentOptions)
        viewModel.liveDataWithdrawResponse.observe(master, observerWithdraw)
        viewModel.liveDataHeaderInfo.observe(master, observerHeaderInfoResponse)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun initializeWidgets() {
        binding.tvBalance.text              = PlayerInfo.getPlayerTotalBalance()
        binding.tvWithdrawAbleBalance.text  = HtmlCompat.fromHtml(getString(R.string.withdrawable_amount) + " <b>" +  PlayerInfo.getPlayerWithdrawalAbleBalance() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        lifecycleScope.launch {
            delay(100)
            setMinMaxWithdrawAmount()
            binding.cardBalance.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction {
                binding.cardBalance.visibility      = View.VISIBLE
                binding.cardBalance.animate().scaleX(1f).scaleY(1f).setDuration(600).withEndAction { }
            }

            val animation = TranslateAnimation(0.0f, 0.0f, 120.0f, 0.0f)
            animation.duration      = 300
            animation.repeatCount   = 0
            animation.fillAfter     = false
            animation.setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    viewModel.callPaymentOptionsApi()
                }

                override fun onAnimationRepeat(p0: Animation?) {}
            })

            binding.llContainer.visibility = View.VISIBLE
            binding.llContainer.startAnimation(animation)
        }
    }

    private fun setListeners() {
        binding.tvAddNewBank.setOnClickListener {
            hideKeyboard()
            master.openFragment(AddBankNonFirstTimeFragment(), "AddBankNonFirstTimeFragment", true, isFadeAnimation = true)
        }

        binding.btnWithdraw.setOnClickListener {
            hideKeyboard()
            if (validateInputs()) {
                val paymentAccId    = mSelectedPaymentAccId
                val paymentTypeCode = mSelectedPayTypeMap.payTypeCode
                val paymentTypeId   = mSelectedPayTypeMap.payTypeId
                val subTypeId       = mSelectedPayTypeMap.subTypeCode

                val request = WithdrawalRequest(amount = binding.etAmount.getTrimText(), paymentAccId = paymentAccId, paymentTypeCode = paymentTypeCode, paymentTypeId = paymentTypeId, subTypeId = subTypeId.toInt())
                viewModel.callWithdrawApi(request)
            }
        }
    }

    private fun validateInputs() : Boolean {
        if (!this::mSelectedPayTypeMap.isInitialized) {
            master.showToast(getString(R.string.select_account))
            return false
        }

        if (binding.etAmount.getTrimText().isBlank()) {
            binding.tilAmount.putError(getString(R.string.enter_valid_amount))
            return false
        }

        try {
            val amount = binding.etAmount.getTrimText().toDouble()
            if (amount < mMinimumWithdrawAmount || amount > mMaximumWithdrawAmount) {
                binding.tilAmount.putError(getString(R.string.enter_valid_amount))
                return false
            }
        } catch (e: Exception) {
            binding.tilAmount.putError(getString(R.string.enter_valid_amount))
            return false
        }

        return true
    }

    private fun setMinMaxWithdrawAmount() {
        val minimum = getString(R.string.minimum_withdrawal_amount) + " <b>" + PlayerInfo.getCurrency() + " " + getFormattedAmount(mMinimumWithdrawAmount) + "</b> " + getString(R.string.and_the)
        val maximum = getString(R.string.maximum_withdrawal_amount_is) + " <b>" + PlayerInfo.getCurrency() + " " + getFormattedAmount(mMaximumWithdrawAmount) + "</b>"
        binding.tvMinWithdrawAmount.text = HtmlCompat.fromHtml(minimum, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tvMaxWithdrawAmount.text = HtmlCompat.fromHtml(maximum, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun hideKeyboard() {
        binding.btnWithdraw.hideKeyboard()
    }

    override fun setToolbarElements() {

    }

    private val observerPaymentOptions = Observer<ResponseStatus<JSONObject>> { responseStatus ->
        when (responseStatus) {
            is ResponseStatus.Success -> {
                handlePaymentOptionsResponseForWithdraw(PaymentOptionsResponseParser().handleResponse(responseStatus.response))
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, CASHIER, responseStatus.errorCode)
                ErrorSheet(getString(R.string.withdrawal_error), errorMessage, getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.withdrawal_error), getString(responseStatus.errorMessageCode), getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun handlePaymentOptionsResponseForWithdraw(paymentOptions: PaymentOptionsResponse?) {
        paymentOptions?.listPayTypeMap?.let { list: ArrayList<PaymentOptionsResponse.PayTypeMap> ->
            if (list.isNotEmpty()) {
                val accountList = ArrayList<Pair<PaymentOptionsResponse.PayTypeMap.AccountDetail, PaymentOptionsResponse.PayTypeMap>>()
                list.forEach { payTypeMap ->
                    payTypeMap.accountDetail?.forEach { accountDetail ->
                        accountList.add(Pair(accountDetail, payTypeMap))
                    }
                }

                if (accountList.isEmpty()) {
                    binding.tvSelectAccount.visibility  = View.INVISIBLE
                    binding.rvAccounts.visibility       = View.INVISIBLE
                } else {
                    binding.tvSelectAccount.visibility  = View.VISIBLE
                    binding.rvAccounts.visibility       = View.VISIBLE

                    val accountAdapter = AccountAdapter(master, accountList) { payType , paymentAccId ->
                        mSelectedPayTypeMap     = payType
                        mSelectedPaymentAccId   = paymentAccId
                        mMinimumWithdrawAmount  = mSelectedPayTypeMap.minValue
                        mMaximumWithdrawAmount  = mSelectedPayTypeMap.maxValue
                        setMinMaxWithdrawAmount()
                    }

                    binding.rvAccounts.apply {
                        layoutManager   = LinearLayoutManager(master)
                        setHasFixedSize(true)
                        adapter         = accountAdapter
                    }
                }
                binding.btnWithdraw.visibility  = View.VISIBLE
                binding.tvMaxAccount.visibility = View.VISIBLE

                if (accountList.size > 2)
                    binding.tvAddNewBank.visibility = View.GONE
                else
                    binding.tvAddNewBank.visibility = View.VISIBLE
            } else {
                ErrorSheet(getString(R.string.withdrawal_error), getString(R.string.some_internal_error), getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        } ?: run {
            ErrorSheet(getString(R.string.withdrawal_error), getString(R.string.some_technical_issue), getString(R.string.close)) { master.onBackPressed() }.apply {
                show(master.supportFragmentManager, ErrorSheet.TAG)
            }
        }
    }

    private val observerWithdraw = Observer<ResponseStatus<WithdrawResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                mWithdrawResponse = it.response
                viewModel.callHeaderInfoApi()
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, CASHIER, it.errorCode)
                ErrorSheet(getString(R.string.withdrawal_error), errorMessage, getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.withdrawal_error), getString(it.errorMessageCode), getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerHeaderInfoResponse = Observer<ResponseStatus<HeaderInfoResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val response: HeaderInfoResponse = it.response
                response.unreadMsgCount?.let { unreadCount -> PlayerInfo.setBadgeCount(master, unreadCount) }
                PlayerInfo.setBalance(master, response)
                master.setToolbarElements()
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, WEAVER, it.errorCode)
                Log.e("log", "Header Info Api Error: $errorMessage")
            }

            is ResponseStatus.TechnicalError -> Log.e("log", "Header Info API Error: ${getString(it.errorMessageCode)}")
        }

        val txnId   = mWithdrawResponse.txnId ?: getString(R.string.na)
        val txnDate = mWithdrawResponse.txnDate ?: getString(R.string.na)
        WithdrawalRequestRaiseSheet(binding.etAmount.getTrimText(), txnId, txnDate) { master.supportFragmentManager.popBackStack() }.apply {
            show(master.supportFragmentManager, WithdrawalRequestRaiseSheet.TAG)
        }
    }
}