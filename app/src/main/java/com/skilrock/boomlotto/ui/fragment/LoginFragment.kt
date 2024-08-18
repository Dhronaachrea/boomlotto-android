package com.skilrock.boomlotto.ui.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.contracts.CountrySelectionContract
import com.skilrock.boomlotto.databinding.FragmentLoginBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.LoginOtpResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : BaseLoginFragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var selectedCountry: CountryListResponse.Data
    private lateinit var countryList: ArrayList<CountryListResponse.Data?>
    private var isFlagClickAllowed = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setTextWatcher()
        setTermsAndConditions()
        callApi()
        setOnClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataLoginOpt.observe(master, observerOtp)
        viewModel.liveDataLoginCountry.observe(master, observerCountryList)
    }

    private fun callApi() {
        if (!this::countryList.isInitialized || countryList.isEmpty())
            viewModel.callCountryListApi()

        if (this::selectedCountry.isInitialized) {
            binding.tvFlag.text         = selectedCountry.flag
            binding.tvFlag.visibility   = View.VISIBLE
        }
    }

    private val activityResultContract = registerForActivityResult(CountrySelectionContract()) {
        it?.let { country ->
            Log.d("log", "Flag: ${country.flag}")
            binding.tvFlag.text = country.flag
            viewModel.code.value = country.isdCode
            selectedCountry = country
            lifecycleScope.launch {
                delay(200)
                animateFlag(binding.llFlag)
            }
        }
    }

    private fun animateFlag(view: View) {
        binding.tvDownArrow.visibility = View.INVISIBLE
        view.animate().scaleX(1.2f).scaleY(1.3f).setDuration(300).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(300).withEndAction {
                view.animate().scaleX(1.2f).scaleY(1.3f).setDuration(300).withEndAction {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(300).withEndAction {
                        binding.tvDownArrow.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.llFlag.setOnClickListener {
            if (isFlagClickAllowed) {
                activityResultContract.launch(countryList)
                master.overridePendingTransition(R.anim.activity_fade_in_fast, R.anim.activity_fade_out_fast)
            }
        }

        binding.etMobile.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (binding.btnRequestOtp.isEnabled)
                        binding.btnRequestOtp.performClick()
                    true
                }
                else -> false
            }
        }

        binding.btnRequestOtp.setOnClickListener {
            if (validateOnButtonClick()) {
                viewModel.callOtpApi()
            }
        }

        binding.etMobile.setOnFocusChangeListener { _, flag ->
            if (flag)
                binding.scrollView.postDelayed({ binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 200)
        }

        binding.etMobile.setOnClickListener {
            binding.scrollView.postDelayed({ binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 200)
        }
    }

    private val observerCountryList = Observer<ResponseStatus<CountryListResponse>> {
        when (it) {
            is ResponseStatus.Success -> operateCountryListResponse(it.response)

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
                ErrorSheet(getString(R.string.login_error), errorMessage, master.getString(R.string.close)) { master.finish() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.login_error), getString(it.errorMessageCode), master.getString(R.string.close)) { master.finish() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun operateCountryListResponse(response: CountryListResponse) {
        response.data?.let { list: ArrayList<CountryListResponse.Data?> ->
            when {
                list.isEmpty() -> {
                    master.showToast(getString(R.string.data_not_received_001))
                    binding.tvDownArrow.visibility  = View.GONE
                    isFlagClickAllowed              = false
                    master.finish()
                }
                list.size == 1 -> {
                    binding.tvDownArrow.visibility  = View.GONE
                    setCountryList(list, false)
                }
                else -> setCountryList(list, true)
            }
        } ?: run {
            master.showToast(getString(R.string.data_not_received_002))
            master.finish()
        }
    }

    private fun setCountryList(list: ArrayList<CountryListResponse.Data?>, isClickable: Boolean) {
        isFlagClickAllowed          = isClickable
        countryList                 = list
        selectedCountry             = list.firstOrNull { it?.isDefault == true } ?: list.filterNotNull().first()
        viewModel.code.value        = selectedCountry.isdCode
        binding.tvFlag.text         = selectedCountry.flag
        binding.tvFlag.visibility   = View.VISIBLE
    }

    private val observerOtp = Observer<ResponseStatus<LoginOtpResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                if (BuildConfig.BUILD_TYPE == BUILD_TYPE_UAT) {
                    val text = "Your OTP to login is ${it.response.data?.mobVerificationCode}. Don't share it with any one."
                    showNotification(master, text)
                }

                it.response.data?.run {
                    countryCode = selectedCountry.countryCode ?: ""
                    isdCode     = selectedCountry.isdCode ?: ""
                    mobileNumberWithoutCountryCode = binding.etMobile.getTrimText()
                }

                val direction: NavDirections =
                    LoginFragmentDirections.actionLoginFragmentToLoginOtpFragment(it.response)

                master.mNavController.navigate(direction)
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

    override fun hideKeyboard() {
        binding.btnRequestOtp.hideKeyboard()
    }

    private fun showNotification(context: Context, body: String) {
        val notificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "Channel Name"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_icon_boom)
            .setContentTitle("Boom")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        notificationManager.notify(notificationId, mBuilder.build())
    }

    private fun  setTermsAndConditions() {
        val text = getString(R.string.terms_and_conditions_complete_text)
        binding.tvTermsAndConditions.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setTextWatcher() {
        binding.etMobile.afterTextChanged {
            binding.tvError.text = ""
            binding.separator.setBackgroundColor(Color.parseColor("#dce9f5"))
            binding.llMobileNumberBox.background = ContextCompat.getDrawable(master, R.drawable.login_country_view_outline)
            binding.btnRequestOtp.isActivated = it.length >= 8
        }
    }

    private fun validateOnButtonClick() : Boolean {
        if (binding.etMobile.getTrimText().length < 8) {
            binding.llMobileNumberBox.background = ContextCompat.getDrawable(master, R.drawable.login_country_view_outline_error)
            binding.separator.setBackgroundColor(ContextCompat.getColor(master, R.color.color_app_pink))
            binding.llMobileNumberBox.startAnimation(shakeError())
            binding.tvError.text = getString(R.string.please_enter_valid_mobile_number)
            return false
        }

        return true
    }

}