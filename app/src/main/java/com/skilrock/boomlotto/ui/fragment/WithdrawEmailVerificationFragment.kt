package com.skilrock.boomlotto.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentWithdrawEmailVerificationBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.response.EmailOtpResponse
import com.skilrock.boomlotto.models.response.PlayerProfileResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.WithdrawEmailVerificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WithdrawEmailVerificationFragment : BaseFragment() {

    private lateinit var binding: FragmentWithdrawEmailVerificationBinding
    private lateinit var viewModel: WithdrawEmailVerificationViewModel
    private var isOtpSent = false
    private var isIdVerified = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdraw_email_verification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        callProfileApi()
        initializeWidgets()
        checkEmailStatus()
        setListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[WithdrawEmailVerificationViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataEmailError.observe(master, observerEmailError)
        viewModel.liveDataAmountError.observe(master, observerAmountError)
        viewModel.liveDataOtpError.observe(master, observerOtpError)
        viewModel.liveDataEmailOtp.observe(master, observerOtp)
        viewModel.liveDataOtpLoader.observe(master, observerOtpLoader)
        viewModel.liveDataEmailVerification.observe(master, observerEmailVerification)
        viewModel.liveDataPlayerProfile.observe(master, observerProfile)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun callProfileApi() {
        viewModel.callPlayerProfileApi()
    }

    private fun initializeWidgets() {
        binding.tvBalance.text              = PlayerInfo.getPlayerTotalBalance()
        binding.tvAmountIn.text             = ("(" + getString(R.string.amount_in) + " " + PlayerInfo.getCurrency() + ")")
        binding.tvWithdrawAbleBalance.text  = HtmlCompat.fromHtml(getString(R.string.withdrawable_amount) + " <b>" +  PlayerInfo.getPlayerWithdrawalAbleBalance() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        lifecycleScope.launch {
            delay(100)
            binding.cardBalance.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction {
                binding.cardBalance.visibility      = View.VISIBLE
                binding.cardBalance.animate().scaleX(1f).scaleY(1f).setDuration(600).withEndAction { }
            }

            val animation = TranslateAnimation(0.0f, 0.0f, 120.0f, 0.0f)
            animation.duration      = 300
            animation.repeatCount   = 0
            animation.fillAfter     = false

            binding.llContainer.visibility = View.VISIBLE
            binding.llContainer.startAnimation(animation)
        }
    }

    private fun setListeners() {
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.llEmail.background = ContextCompat.getDrawable(master, R.drawable.withdraw_email_focused)
            else
                binding.llEmail.background = ContextCompat.getDrawable(master, R.drawable.withdraw_email_unfocused)
        }

        binding.etEmail.afterTextChanged {
            binding.tvOtpSentText.setTextColor(Color.parseColor("#9900004c"))
            binding.llEmail.background          = ContextCompat.getDrawable(master, R.drawable.withdraw_email_focused)
            binding.tvOtpSentText.visibility    = View.GONE
            watchButtonActiveStatus()
        }

        binding.etAmount.afterTextChanged { watchButtonActiveStatus() }

        binding.etOtp.afterTextChanged { watchButtonActiveStatus() }

        binding.btnContinue.setOnClickListener {
            hideKeyboard()
            if (viewModel.validateInputs()) {
                if (PlayerInfo.getPlayerEmailId().trim().isBlank()) {
                    viewModel.callEmailVerificationApi()
                } else {
                    val bundleAmount = Bundle()
                    bundleAmount.putString("amount", binding.etAmount.getTrimText())
                    bundleAmount.putBoolean("isIdVerified", isIdVerified)
                    master.mFirstTimeWithdrawAmount = binding.etAmount.getTrimText().toDouble()
                    master.openFragment(WithdrawIdAndBankVerificationFragment(), "WithdrawIdAndBankVerificationFragment", true, args = bundleAmount, popPreviousFragment = false)
                }
            }
        }
    }

    private fun checkEmailStatus() {
        binding.llEmailBox.visibility = if (PlayerInfo.getPlayerEmailId().isBlank()) View.VISIBLE else View.GONE
    }

    private val observerEmailError = Observer<Unit> {
        binding.llEmail.startAnimation(shakeError())
        binding.tvOtpSentText.setTextColor(ContextCompat.getColor(master, R.color.color_app_pink))

        binding.llEmail.background          = ContextCompat.getDrawable(master, R.drawable.withdraw_email_error)
        binding.tvOtpSentText.text          = getString(R.string.please_enter_valid_email_id)
        binding.tvOtpSentText.visibility    = View.VISIBLE
    }

    private val observerAmountError = Observer<Unit> {
        binding.tilAmount.putError(getString(R.string.enter_valid_amount))
        binding.btnContinue.isActivated = false
    }

    private val observerOtpError = Observer<Unit> {
        binding.tilOtp.putError(getString(R.string.enter_valid_otp))
        binding.btnContinue.isActivated = false
    }

    private val observerOtp = Observer<ResponseStatus<EmailOtpResponse>> { response ->
        when (response) {
            is ResponseStatus.Success -> {
                isOtpSent                           = true
                viewModel.isOtpClickAllowed         = false
                binding.etEmail.isEnabled           = false
                binding.etEmail.isFocusable         = false
                binding.cardGetOtp.foreground       = null
                binding.tvOtpSentText.visibility    = View.VISIBLE
                binding.tvOtpSentText.text          = getString(R.string.we_have_sent_the_verification_code_to_your_email_id)

                binding.tvOtpSentText.setTextColor(Color.parseColor("#9900004c"))
                binding.cardGetOtp.setCardBackgroundColor(Color.parseColor("#ff6aa6"))
                binding.etEmail.setTextColor(Color.parseColor("#8486aa"))
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, response.errorCode)
                ErrorSheet(getString(R.string.withdrawal_error), errorMessage, master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.withdrawal_error), getString(response.errorMessageCode), master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerEmailVerification = Observer<ResponseStatus<EmailOtpResponse>> { response ->
        when (response) {
            is ResponseStatus.Success -> {
                PlayerInfo.setEmailId(master, binding.etEmail.getTrimText())
                val text = getString(R.string.your_email_id) + " <b>" + binding.etEmail.getTrimText() + "</b> " + getString(R.string.is_verified_successfully)
                ErrorSheet(getString(R.string.email_verification), text, master.getString(R.string.continue_)) {
                    lifecycleScope.launch {
                        delay(400)
                        binding.llEmailBox.visibility = View.GONE
                        val bundleAmount = Bundle()
                        bundleAmount.putString("amount", binding.etAmount.getTrimText())
                        bundleAmount.putBoolean("isIdVerified", isIdVerified)
                        master.mFirstTimeWithdrawAmount = binding.etAmount.getTrimText().toDouble()
                        master.openFragment(WithdrawIdAndBankVerificationFragment(), "WithdrawIdAndBankVerificationFragment", true, args = bundleAmount, popPreviousFragment = false)
                    }
                }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, response.errorCode)
                ErrorSheet(getString(R.string.withdrawal_error), errorMessage, master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.withdrawal_error), getString(response.errorMessageCode), master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerProfile = Observer<ResponseStatus<PlayerProfileResponse>> { response ->
        when (response) {
            is ResponseStatus.Success -> {
                isIdVerified = response.response.ramPlayerInfo?.idVerified == "UPLOADED"
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, response.errorCode)
                ErrorSheet(getString(R.string.withdrawal_error), errorMessage, master.getString(R.string.close)) { master.supportFragmentManager.popBackStack() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.withdrawal_error), getString(response.errorMessageCode), master.getString(R.string.close)) { master.supportFragmentManager.popBackStack() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    override fun hideKeyboard() {
        binding.btnContinue.hideKeyboard()
    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.withdrawal), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    private val observerOtpLoader = Observer<Boolean> {
        if (it) {
            binding.cardGetOtp.visibility = View.GONE
            binding.progressBarOtp.animate().alpha(1f).setDuration(20).withEndAction {
                master.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                binding.progressBarOtp.visibility   = View.VISIBLE
            }
        }
        else {
            binding.progressBarOtp.animate().alpha(0f).setDuration(20).withEndAction {
                binding.progressBarOtp.visibility   = View.GONE
                binding.cardGetOtp.visibility       = View.VISIBLE
                master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private fun watchButtonActiveStatus() {
        try {
            if (binding.etAmount.getTrimText().isBlank() || binding.etAmount.getTrimText().toDouble() <= 0) {
                binding.btnContinue.isActivated = false
                return
            }
        } catch (e: Exception) {
            binding.btnContinue.isActivated = false
            return
        }

        if (PlayerInfo.getPlayerEmailId().isNotBlank()) {
            binding.btnContinue.isActivated = true
        } else {
            if (!isOtpSent) {
                binding.btnContinue.isActivated = false
                return
            }

            if (binding.etOtp.getTrimText().length != 4) {
                binding.btnContinue.isActivated = false
                return
            }
        }

        binding.btnContinue.isActivated = true
    }
}