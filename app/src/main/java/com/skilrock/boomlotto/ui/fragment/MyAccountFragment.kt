package com.skilrock.boomlotto.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentMyAccountBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.response.HeaderInfoResponse
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse
import com.skilrock.boomlotto.ui.activity.DepositActivity
import com.skilrock.boomlotto.ui.activity.IdVerificationActivity
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.MyAccountViewModel
import org.json.JSONObject

class MyAccountFragment : BaseFragment() {

    private lateinit var binding: FragmentMyAccountBinding
    private lateinit var viewModel: MyAccountViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements()
        setViewModel()
        initializeWidgets()
        setOnClickListeners()
     }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[MyAccountViewModel::class.java]
        binding.lifecycleOwner = this

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataPaymentOptions.observe(master, observerPaymentOptions)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
        viewModel.liveDataHeaderInfo.observe(master, observerBalance)
    }

    private fun initializeWidgets() {
        setPlayerDetails()
        setConditionsForKyc()
        setAnimationFromLeft()
        setAnimationFromRight()
        setAnimationFromBottom()
        setAnimationForCardBalance()
    }

    private fun setAnimationFromLeft() {
        val animationFromLeft = TranslateAnimation(-800.0f, 0.0f, 0.0f, 0.0f)
        animationFromLeft.duration      = 700
        animationFromLeft.repeatCount   = 0
        animationFromLeft.fillAfter     = false
        binding.cardMyTickets.startAnimation(animationFromLeft)
        binding.cardMyTickets.visibility = View.VISIBLE
    }

    private fun setAnimationFromRight() {
        val animationFromRight = TranslateAnimation(800.0f, 0.0f, 0.0f, 0.0f)
        animationFromRight.duration      = 700
        animationFromRight.repeatCount   = 0
        animationFromRight.fillAfter     = false
        binding.cardMyTransactions.startAnimation(animationFromRight)
        binding.cardMyTransactions.visibility = View.VISIBLE
    }

    private fun setAnimationFromBottom() {
        val animationFromBottom = TranslateAnimation(0.0f, 0.0f, 800.0f, 0.0f)
        animationFromBottom.duration      = 700
        animationFromBottom.repeatCount   = 0
        animationFromBottom.fillAfter     = false
        binding.llEmailAndViewProfile.startAnimation(animationFromBottom)
        binding.llEmailAndViewProfile.visibility = View.VISIBLE
    }

    private fun setAnimationForCardBalance() {
        binding.cardBalance.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction {
            binding.cardBalance.visibility = View.VISIBLE
            binding.cardBalance.animate().scaleX(1f).scaleY(1f).setDuration(600).withEndAction { }
        }
    }

    private fun setConditionsForKyc() {
        this.arguments?.let{
            val profileStatus: String? = it.getString("profileStatus")
            if(profileStatus != null) {
                if (profileStatus.equals("ACTIVE", true)) setKycVerified() else setKycPending()
            } else setKycPending()
        }
    }

    private fun setKycVerified() {
        context?.let{ colorContext -> binding.tvKycPending.setTextColor(ContextCompat.getColor(colorContext, R.color.color_app_green)) }
        binding.ivKycPendingIcon.setImageResource(R.drawable.outline_check_circle_outline_24)
        binding.ivKycPendingIcon.visibility = View.VISIBLE
        binding.tvKycPending.text           = getString(R.string.kyc_verified)
        binding.tvKycPending.visibility     = View.VISIBLE
    }

    private fun setKycPending() {
        context?.let{ colorContext -> binding.tvKycPending.setTextColor(ContextCompat.getColor(colorContext, R.color.color_app_orange)) }
        binding.ivKycPendingIcon.setImageResource(R.drawable.outline_error_24)
        binding.ivKycPendingIcon.visibility = View.VISIBLE
        binding.tvKycPending.text           = context?.getString(R.string.kyc_pending)
        binding.tvKycPending.visibility = View.VISIBLE
    }

    private fun setPlayerDetails() {
        binding.tvBalance.text = HtmlCompat.fromHtml(PlayerInfo.getPlayerTotalBalanceBold(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        val withdrawBalance = "${master.getString(R.string.withdrawable_amount)} <b>${PlayerInfo.getPlayerWithdrawalAbleBalance()}</b>"
        binding.tvWithdrawAbleBalance.text = HtmlCompat.fromHtml(withdrawBalance, HtmlCompat.FROM_HTML_MODE_LEGACY)

        when {
            PlayerInfo.getPlayerFirstName().isNotEmpty() && PlayerInfo.getPlayerLastName().isNotEmpty() -> {
                val userNameOnMyAccount = "${PlayerInfo.getPlayerFirstName()} ${PlayerInfo.getPlayerLastName()}"
                binding.tvUserNameOnMyAccount.text = userNameOnMyAccount
            }

            PlayerInfo.getPlayerFirstName().isEmpty() && PlayerInfo.getPlayerLastName().isNotEmpty() -> {
                binding.tvUserNameOnMyAccount.text = PlayerInfo.getPlayerLastName()
            }

            PlayerInfo.getPlayerFirstName().isNotEmpty() && PlayerInfo.getPlayerLastName().isEmpty() -> {
                binding.tvUserNameOnMyAccount.text = PlayerInfo.getPlayerFirstName()
            }

            else -> {
                binding.tvUserNameOnMyAccount.text = PlayerInfo.getPlayerUserName()
            }
        }
    }

    private fun setOnClickListeners() {
        binding.cardViewProfile.setOnClickListener {
            master.openFragment(MyProfileFragment(), "MyProfileFragment", true, popPreviousFragment = false)
        }

        binding.cardUpdateEmail.setOnClickListener {
            master.openFragment(PlayerEmailUpdateFragment(), "PlayerEmailUpdateFragment", true, popPreviousFragment = false)
        }

        binding.cardMyTransactions.setOnClickListener {
            master.openFragment(MyTransactionsFragment(), "MyTransactionsFragment", true, popPreviousFragment = false, isFadeAnimation = true)
        }

        binding.cardMyTickets.setOnClickListener {
            master.openFragment(MyTicketsFragment(), "MyTicketsFragment", true, popPreviousFragment = false, isFadeAnimation = true)
        }

        binding.btnAddMoney.setOnClickListener {
            if (PlayerInfo.isIdVerified()) {
                startActivity(Intent(master, DepositActivity::class.java))
                master.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                startActivity(Intent(master, IdVerificationActivity::class.java))
                master.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        binding.btnWithdraw.setOnClickListener {
            viewModel.callPaymentOptionsApi()
        }

        binding.ivRefresh.setOnClickListener {
            val rotateAnimation = RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotateAnimation.duration = 500
            rotateAnimation.repeatCount = 0
            binding.ivRefresh.startAnimation(rotateAnimation)
            viewModel.callHeaderInfoApi("REFRESH_BALANCE")
        }
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.my_account), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    private val observerBalance = Observer<ResponseStatus<HeaderInfoResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val response: HeaderInfoResponse = it.response
                response.unreadMsgCount?.let { unreadCount -> PlayerInfo.setBadgeCount(master, unreadCount) }
                PlayerInfo.setBalance(master, response)
                master.setToolbarElements()
                val refreshedBalance = "${BuildConfig.CURRENCY_CODE} <b>${getFormattedAmount(response.cashbal.toString().toDouble())}</b>"
                binding.tvBalance.text = HtmlCompat.fromHtml(refreshedBalance, HtmlCompat.FROM_HTML_MODE_LEGACY)
                val withdrawBalance = "${master.getString(R.string.withdrawable_amount)} <b>${PlayerInfo.getPlayerWithdrawalAbleBalance()}</b>"
                binding.tvWithdrawAbleBalance.text = HtmlCompat.fromHtml(withdrawBalance, HtmlCompat.FROM_HTML_MODE_LEGACY)

                binding.tvBalance.animate().scaleX(.2F).scaleY(.2F).setDuration(200).withEndAction {
                    binding.tvBalance.animate().scaleX(1F).scaleY(1F).duration = 200
                }
                binding.tvWithdrawAbleBalance.animate().scaleX(.2F).scaleY(.2F).setDuration(200).withEndAction {
                    binding.tvWithdrawAbleBalance.animate().scaleX(1F).scaleY(1F).duration = 200
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, WEAVER, it.errorCode)
                Log.e("log", "Header Info Api Error: $errorMessage")
            }

            is ResponseStatus.TechnicalError -> Log.e("log", "Header Info API Error: ${getString(it.errorMessageCode)}")
        }
    }

    private val observerPaymentOptions = Observer<ResponseStatus<JSONObject>> { responseStatus ->
        when (responseStatus) {
            is ResponseStatus.Success -> {
                val paymentOptions = PaymentOptionsResponseParser().handleResponse(responseStatus.response)
                Log.v("log", "Payment Options Model: $paymentOptions")
                handlePaymentOptionsResponseForWithdraw(paymentOptions)
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
                Log.v("log", "(PlayerInfo.getRawWithdrawalAbleBalance() < list[0].minValue), ${PlayerInfo.getRawWithdrawalAbleBalance()} < ${list[0].minValue}")
                if (PlayerInfo.getRawWithdrawalAbleBalance() < list[0].minValue) {
                    master.openFragment(WithdrawLowBalanceFragment(), "WithdrawLowBalanceFragment", true, isFadeAnimation = true, popPreviousFragment = false)
                } else {
                    if (!PlayerInfo.isBankVerified()) {
                        master.openFragment(WithdrawEmailVerificationFragment(), "WithdrawEmailVerificationFragment", true, isFadeAnimation = true, popPreviousFragment = false)
                    } else {
                        master.openFragment(WithdrawalViewPagerFragment(), "WithdrawalViewPagerFragment", true, isFadeAnimation = true, popPreviousFragment = false)
                    }
                }
            } else {
                ErrorSheet(getString(R.string.login_error), getString(R.string.some_internal_error), getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        } ?: run {
            ErrorSheet(getString(R.string.login_error), getString(R.string.some_technical_issue), getString(R.string.close)) {  }.apply {
                show(master.supportFragmentManager, ErrorSheet.TAG)
            }
        }
    }
}