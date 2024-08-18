package com.skilrock.boomlotto.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentLoginOtpBinding
import com.skilrock.boomlotto.dialogs.ConfirmationSheet
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.RegistrationSheet
import com.skilrock.boomlotto.models.request.LoginRequest
import com.skilrock.boomlotto.models.response.BonusResponse
import com.skilrock.boomlotto.models.response.LoginOtpResponse
import com.skilrock.boomlotto.models.response.LoginResponse
import com.skilrock.boomlotto.models.response.ReferCodeResponse
import com.skilrock.boomlotto.ui.activity.DepositActivity
import com.skilrock.boomlotto.ui.activity.IdVerificationActivity
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.LoginOtpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginOtpFragment : BaseLoginFragment() {

    private lateinit var binding: FragmentLoginOtpBinding
    private lateinit var viewModel: LoginOtpViewModel
    private lateinit var loginOtpResponse: LoginOtpResponse.Data

    private var referralCode            = ""
    private var isResendClickAllowed    = false
    private val resendOtpTimer          = ResendOtpTimer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_otp, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        receiveParameters()
        startResendOtpTimer()
        setEditTexts()
        setClickListeners()
        setOtpFocusListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[LoginOtpViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataReferralLoader.observe(master, observerReferralLoader)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataLogin.observe(master, observerLogin)
        viewModel.liveDataLoginOpt.observe(master, observerOtp)
        viewModel.liveDataReferCode.observe(master, observerReferralCode)
        viewModel.liveDataBonus.observe(master, observeBonus)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun receiveParameters() {
        arguments?.let {
            val response: LoginOtpResponse = LoginOtpFragmentArgs.fromBundle(it).loginOtpResponse
            response.data?.let { res ->
                loginOtpResponse = res
                binding.tvMobile.text = ("${getString(R.string.on)} ${loginOtpResponse.isdCode} - ${loginOtpResponse.mobileNumberWithoutCountryCode}")

                if (loginOtpResponse.otpActionType == "REGISTRATION") {
                    binding.llReferralCode.visibility = View.VISIBLE
                } else {
                    binding.llReferralCode.visibility = View.GONE
                }
            } ?: run {
                master.showToast(getString(R.string.some_technical_issue))
                master.finish()
                master.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }

    private fun startResendOtpTimer() {
        resendOtpTimer.start()
    }

    override fun hideKeyboard() {
        binding.btnSubmit.hideKeyboard()
    }

    private fun setEditTexts() {
        val listEditText: List<AppCompatEditText> = listOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        binding.etOtp1.addTextChangedListener(OtpTextWatcher(binding.etOtp1, listEditText, ::setLoginButtonMode))
        binding.etOtp2.addTextChangedListener(OtpTextWatcher(binding.etOtp2, listEditText, ::setLoginButtonMode))
        binding.etOtp3.addTextChangedListener(OtpTextWatcher(binding.etOtp3, listEditText, ::setLoginButtonMode))
        binding.etOtp4.addTextChangedListener(OtpTextWatcher(binding.etOtp4, listEditText, ::setLoginButtonMode))

        binding.etOtp4.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (binding.btnSubmit.isEnabled)
                        binding.btnSubmit.performClick()
                    true
                }
                else -> false
            }
        }

        binding.etReferralCode.afterTextChanged {
            binding.ivReferral.visibility       = View.GONE
            binding.cardVerify.visibility       = View.VISIBLE
            binding.tvErrorReferralCode.text    = ""
            binding.llReferralBox.background    = ContextCompat.getDrawable(master, R.drawable.referral_view_outline)
        }
    }

    private fun setLoginButtonMode(isEnabled: Boolean, isLastOtpBox: Boolean) {
        binding.btnSubmit.isActivated   = isEnabled
        binding.tvError.text            = ""
    }

    private fun setClickListeners() {
        binding.btnSubmit.setOnClickListener {
            if (validateOtpInputs()) {

                if (loginOtpResponse.otpActionType == "REGISTRATION") {
                    if (binding.llReferralBox.visibility == View.VISIBLE && referralCode == "") {
                        val message = getString(R.string.do_you_want_to_continue_without_referral_code)
                        ConfirmationSheet(getString(R.string.login), message, getString(R.string.no), getString(R.string.yes), { }) { loginWithoutReferral() }.apply {
                            show(master.supportFragmentManager, ConfirmationSheet.TAG)
                        }
                    } else {
                        if (referralCode != "") {
                            val otp = binding.etOtp1.getTrimText() + binding.etOtp2.getTrimText() + binding.etOtp3.getTrimText() + binding.etOtp4.getTrimText()
                            val loginRequest = LoginRequest(mobileNo = loginOtpResponse.mobileNo, otp = otp, countryCode = loginOtpResponse.countryCode, referCode = referralCode)
                            viewModel.callLoginApi(loginRequest)
                        } else {
                            loginWithoutReferral()
                        }
                    }
                } else {
                    loginWithoutReferral()
                }
            }
        }

        binding.tvResend.setOnClickListener {
            if (isResendClickAllowed) {
                loginOtpResponse.mobileNo?.let { mobileNumber ->
                    viewModel.callOtpApi(mobileNumber)
                } ?: run { master.showToast(getString(R.string.some_technical_issue)) }
                resendOtpTimer.start()
                binding.etOtp1.setText("")
                binding.etOtp2.setText("")
                binding.etOtp3.setText("")
                binding.etOtp4.setText("")
                binding.etOtp1.background = ContextCompat.getDrawable(master, R.drawable.otp_box_blank)
                binding.etOtp2.background = ContextCompat.getDrawable(master, R.drawable.otp_box_blank)
                binding.etOtp3.background = ContextCompat.getDrawable(master, R.drawable.otp_box_blank)
                binding.etOtp4.background = ContextCompat.getDrawable(master, R.drawable.otp_box_blank)
            }
        }

        binding.btnHaveReferral.setOnClickListener {
            binding.llReferralBox.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction { }

            binding.btnHaveReferral.animate().scaleX(0f).scaleY(0f).setDuration(400).withEndAction {
                        binding.btnHaveReferral.visibility  = View.GONE
                        binding.llReferralBox.visibility    = View.VISIBLE
                        binding.llReferralBox.animate().scaleX(1f).scaleY(1f).setDuration(400).withEndAction { }
                    }
        }

        binding.cardVerify.setOnClickListener {
            binding.btnSubmit.hideKeyboard()
            if (validateReferral()) {
                binding.cardVerify.visibility = View.GONE
                viewModel.callReferCodeApi(binding.etReferralCode.getTrimText())
            }
        }
    }

    private fun loginWithoutReferral() {
        val otp = binding.etOtp1.getTrimText() + binding.etOtp2.getTrimText() + binding.etOtp3.getTrimText() + binding.etOtp4.getTrimText()
        val loginRequest = LoginRequest(mobileNo = loginOtpResponse.mobileNo, otp = otp, countryCode = loginOtpResponse.countryCode)
        viewModel.callLoginApi(loginRequest)
    }

    private fun validateReferral() : Boolean {
        if (binding.etReferralCode.getTrimText().length < 5) {
            binding.llReferralCode.startAnimation(shakeError())
            master.showToast(getString(R.string.please_enter_complete_referral))
            return false
        }

        return true
    }

    private fun validateOtpInputs() : Boolean {
        if (binding.etOtp1.getTrimText().isBlank()) {
            binding.etOtp1.startAnimation(shakeError())
            binding.tvError.text = getString(R.string.please_enter_otp)
            binding.etOtp1.background = ContextCompat.getDrawable(master, R.drawable.otp_box_error)
            return false
        }
        if (binding.etOtp2.getTrimText().isBlank()) {
            binding.etOtp2.startAnimation(shakeError())
            binding.tvError.text = getString(R.string.please_enter_otp)
            binding.etOtp2.background = ContextCompat.getDrawable(master, R.drawable.otp_box_error)
            return false
        }
        if (binding.etOtp3.getTrimText().isBlank()) {
            binding.etOtp3.startAnimation(shakeError())
            binding.tvError.text = getString(R.string.please_enter_otp)
            binding.etOtp3.background = ContextCompat.getDrawable(master, R.drawable.otp_box_error)
            return false
        }
        if (binding.etOtp4.getTrimText().isBlank()) {
            binding.etOtp4.startAnimation(shakeError())
            binding.tvError.text = getString(R.string.please_enter_otp)
            binding.etOtp4.background = ContextCompat.getDrawable(master, R.drawable.otp_box_error)
            return false
        }
        return true
    }

    private val observerLogin = Observer<ResponseStatus<LoginResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                PlayerInfo.setLoginData(master, it.response)
                PlayerInfo.setBadgeCount(master, it.response.playerLoginInfo?.unreadMsgCount ?: 0)

                if (loginOtpResponse.otpActionType == "REGISTRATION") {
                    viewModel.callBonusApi()
                }
                else {
                    master.finish()
                    master.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
                ErrorSheet(getString(R.string.login_error), errorMessage, master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.login_error), getString(it.errorMessageCode), master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observeBonus = Observer<ResponseStatus<BonusResponse>> {
        var bonusAmount = 0.0
        when(it){
            is ResponseStatus.Success -> {
                val dataList: List<BonusResponse.Data?>? = it.response.data
                if(dataList != null && dataList.isNotEmpty()) bonusAmount = dataList[0]?.receivedBonus ?: 0.0
            }

            is ResponseStatus.Error -> {
                Log.e("OkHttp", "Error: $it")
            }

            is ResponseStatus.TechnicalError -> {
                Log.e("OkHttp", "Technical Error: $it")
            }
        }

        RegistrationSheet(bonusAmount, BuildConfig.CURRENCY_CODE, ::onDepositCallBack) {
            lifecycleScope.launch {
                delay(200)
                master.finish()
                master.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }.apply {
            show(master.supportFragmentManager, RegistrationSheet.TAG)
        }
    }

    private fun onDepositCallBack() {
        if (PlayerInfo.isIdVerified()) {
            startActivity(Intent(master, DepositActivity::class.java))
            master.finish()
            master.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            startActivity(Intent(master, IdVerificationActivity::class.java))
            master.finish()
            master.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun setOtpFocusListeners() {
        binding.etOtp1.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.etOtp1.background = ContextCompat.getDrawable(master, R.drawable.otp_box_focused)
            else {
                if (binding.etOtp1.getTrimText().isBlank())
                    binding.etOtp1.background = ContextCompat.getDrawable(master, R.drawable.otp_box_blank)
                else
                    binding.etOtp1.background = ContextCompat.getDrawable(master, R.drawable.otp_box_filled)
            }
        }

        binding.etOtp2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.etOtp2.background = ContextCompat.getDrawable(master, R.drawable.otp_box_focused)
            else {
                if (binding.etOtp2.getTrimText().isBlank())
                    binding.etOtp2.background = ContextCompat.getDrawable(master, R.drawable.otp_box_blank)
                else
                    binding.etOtp2.background = ContextCompat.getDrawable(master, R.drawable.otp_box_filled)
            }
        }

        binding.etOtp3.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.etOtp3.background = ContextCompat.getDrawable(master, R.drawable.otp_box_focused)
            else {
                if (binding.etOtp3.getTrimText().isBlank())
                    binding.etOtp3.background = ContextCompat.getDrawable(master, R.drawable.otp_box_blank)
                else
                    binding.etOtp3.background = ContextCompat.getDrawable(master, R.drawable.otp_box_filled)
            }
        }

        binding.etOtp4.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.etOtp4.background = ContextCompat.getDrawable(master, R.drawable.otp_box_focused)
            else {
                if (binding.etOtp4.getTrimText().isBlank())
                    binding.etOtp4.background = ContextCompat.getDrawable(master, R.drawable.otp_box_blank)
                else
                    binding.etOtp4.background = ContextCompat.getDrawable(master, R.drawable.otp_box_filled)
            }
        }
    }

    private val observerOtp = Observer<ResponseStatus<LoginOtpResponse>> {
        when (it) {
            is ResponseStatus.Success -> master.showToast(getString(R.string.otp_sent_again))

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
                ErrorSheet(getString(R.string.login_error), errorMessage, master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.login_error), getString(it.errorMessageCode), master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerReferralLoader = Observer<Boolean> {
        if (it) {
            binding.progressBarReferral.animate().alpha(1f).setDuration(20).withEndAction {
                master.window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                binding.progressBarReferral.visibility = View.VISIBLE
            }
        }
        else {
            binding.progressBarReferral.animate().alpha(0f).setDuration(20).withEndAction {
                binding.progressBarReferral.visibility = View.GONE
                master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private val observerReferralCode = Observer<ResponseStatus<ReferCodeResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                binding.ivReferral.visibility = View.VISIBLE
                binding.ivReferral.setImageResource(R.drawable.icon_filled_check)
                binding.tvErrorReferralCode.setTextColor(ContextCompat.getColor(master, R.color.color_app_green))
                referralCode                                    = binding.etReferralCode.getTrimText()
                binding.etReferralCode.isFocusable              = false
                binding.etReferralCode.isClickable              = false
                binding.etReferralCode.isFocusableInTouchMode   = false
                binding.tvErrorReferralCode.text                = getString(R.string.valid_referral_code)
                binding.llReferralBox.background                = ContextCompat.getDrawable(master, R.drawable.referral_view_outline_success)
            }

            is ResponseStatus.Error -> {
                binding.ivReferral.visibility = View.VISIBLE
                binding.ivReferral.setImageResource(R.drawable.icon_filled_cross)
                binding.llReferralCode.startAnimation(shakeError())
                binding.tvErrorReferralCode.setTextColor(ContextCompat.getColor(master, R.color.color_app_orange))
                referralCode                        = ""
                binding.tvErrorReferralCode.text    = getString(R.string.invalid_referral_code)
                binding.llReferralBox.background    = ContextCompat.getDrawable(master, R.drawable.referral_view_outline_error)
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.login_error), getString(it.errorMessageCode), master.getString(R.string.close)) {}.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    inner class ResendOtpTimer(millisInFuture: Long = 30000) : CountDownTimer(millisInFuture, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            isResendClickAllowed            = false
            binding.tvResend.text           = ("${(millisUntilFinished/1000)} ${getString(R.string.sec)}")
            binding.tvResendOtpLabel.text   = getString(R.string.resend_otp_in)
        }

        override fun onFinish() {
            isResendClickAllowed            = true
            binding.tvResend.text           = getString(R.string.resend)
            binding.tvResendOtpLabel.text   = getString(R.string.didn_t_get_the_code_yet)
        }
    }

    override fun onDestroyView() {
        resendOtpTimer.cancel()
        super.onDestroyView()
    }

}