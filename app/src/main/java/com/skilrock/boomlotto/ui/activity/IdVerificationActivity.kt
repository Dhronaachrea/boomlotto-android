package com.skilrock.boomlotto.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.ActivityIdVerificationBinding
import com.skilrock.boomlotto.dialogs.CountrySelectionSheet
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.IdVerificationResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.IdVerificationViewModel
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IdVerificationActivity : BaseActivity(), BaseActivity.HeaderInfoListener {

    private lateinit var binding: ActivityIdVerificationBinding
    private lateinit var viewModel: IdVerificationViewModel
    private lateinit var mCountryList: ArrayList<CountryListResponse.Data?>
    private var isNationalityClickAllowed: Boolean = true
    private var mDocName = "PASSPORT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowStyling()
        setDataBindingAndViewModel()
        binding.toolbar.tbNavigationIcon.setImageResource(R.drawable.icon_back)
        callHeaderInfoApi()
        callCountryListApi()
        setOnClickListeners()
        setTextWatchers()
    }

    override fun onResume() {
        super.onResume()
        setToolbarElements()
    }

    override fun setDataBindingAndViewModel() {
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_id_verification)
        viewModel   = ViewModelProvider(this)[IdVerificationViewModel::class.java]

        binding.lifecycleOwner  = this
        binding.viewModel       = viewModel
        headerInfoListener      = this

        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataLoginCountry.observe(this, observerCountryList)
        viewModel.liveDataIdVerification.observe(this, observerIdVerification)
        viewModel.liveDataNetworkError.observe(this, observerNetworkError)
    }

    private fun callHeaderInfoApi() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isNotEmpty()) {
            viewModel.callHeaderInfoApi()
        }
    }

    private fun callCountryListApi() {
        viewModel.callCountryListApi()
    }

    private fun setOnClickListeners() {
        binding.btnProceed.isActivated = false

        binding.toolbar.llNotification.setOnClickListener {
            startActivity(Intent(this, PlayerInboxActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.eTDob.setOnClickListener {
            hideKeyboard()
            openDobDialog(this, ::onDateOfBirthSelected)
        }

        binding.eTNationality.setOnClickListener {
            hideKeyboard()
            if (isNationalityClickAllowed) {
                CountrySelectionSheet(mCountryList, true, ::onCountryClick).apply {
                    show(supportFragmentManager, CountrySelectionSheet.TAG)
                }
            }
        }

        binding.eTId.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    binding.btnProceed.hideKeyboard()
                    binding.btnProceed.performClick()
                    true
                }
                else -> false
            }
        }

        binding.tvPassportId.setOnClickListener {
            binding.tvPassportId.background = ContextCompat.getDrawable(this, R.drawable.id_number_selected)
            binding.tvEmiratesId.background = ContextCompat.getDrawable(this, R.drawable.id_number_unselected)

            binding.tvPassportId.setTextColor(ContextCompat.getColor(this, R.color.color_app_pink))
            binding.tvEmiratesId.setTextColor(ContextCompat.getColor(this, R.color.color_app_base_blue))

            binding.eTId.hint = getString(R.string.enter_passport_id_number)
            mDocName = "PASSPORT"
        }

        binding.tvEmiratesId.setOnClickListener {
            binding.tvEmiratesId.background = ContextCompat.getDrawable(this, R.drawable.id_number_selected)
            binding.tvPassportId.background = ContextCompat.getDrawable(this, R.drawable.id_number_unselected)

            binding.tvEmiratesId.setTextColor(ContextCompat.getColor(this, R.color.color_app_pink))
            binding.tvPassportId.setTextColor(ContextCompat.getColor(this, R.color.color_app_base_blue))

            binding.eTId.hint = getString(R.string.enter_emirates_id_number)
            mDocName = "EMIRATES_ID"
        }

        binding.btnProceed.setOnClickListener {
            if (validateOnButtonClick()) {
                viewModel.callIdVerificationApi(binding.etFirstName.getTrimText(), binding.etLastName.getTrimText(), binding.eTDob.getTrimText(), binding.eTNationality.getTrimText(), mDocName, binding.eTId.getTrimText())
            }
        }

        binding.toolbar.llDrawerIcon.setOnClickListener { onBackPressed() }
    }

    private fun onDateOfBirthSelected(strDate: String) {
        lifecycleScope.launch {
            delay(300)
            binding.eTDob.setText(strDate)
            animateTextInputLayout(binding.tilDob)
        }
    }

    private fun onCountryClick(country: CountryListResponse.Data?, isNationality: Boolean) {
        lifecycleScope.launch {
            delay(300)
            binding.eTNationality.setText(country?.countryName)
            animateTextInputLayout(binding.tilNationality)
        }
    }

    private fun animateTextInputLayout(til: BoomTextInputLayout) {
        til.animate().scaleX(1f).scaleY(1.2f).setDuration(300).withEndAction {
            til.animate().scaleX(1f).scaleY(1f).setDuration(300).withEndAction {
                til.animate().scaleX(1f).scaleY(1.2f).setDuration(300).withEndAction {
                    til.animate().scaleX(1f).scaleY(1f).setDuration(300).withEndAction {

                    }
                }
            }
        }
    }

    private fun setTextWatchers() {
        binding.etFirstName.afterTextChanged { validateOnTextChange() }
        binding.etLastName.afterTextChanged { validateOnTextChange() }
        binding.eTDob.afterTextChanged { validateOnTextChange() }
        binding.eTNationality.afterTextChanged { validateOnTextChange() }
        binding.eTId.afterTextChanged { validateOnTextChange() }
    }

    private fun validateOnTextChange() {
        val firstName   = binding.etFirstName.getTrimText()
        val lastname    = binding.etLastName.getTrimText()
        val dob         = binding.eTDob.getTrimText()
        val nationality = binding.eTNationality.getTrimText()
        val id          = binding.eTId.getTrimText()

        val flag = firstName.isNotBlank() && lastname.isNotBlank() && dob.isNotBlank() && nationality.isNotBlank() && id.isNotBlank()
        binding.btnProceed.isActivated = flag
    }

    private fun validateOnButtonClick() : Boolean {
        val firstName   = binding.etFirstName.getTrimText()
        val lastname    = binding.etLastName.getTrimText()
        val dob         = binding.eTDob.getTrimText()
        val nationality = binding.eTNationality.getTrimText()
        val id          = binding.eTId.getTrimText()

        if (firstName.isBlank()) {
            binding.tilFirstName.putError(getString(R.string.enter_first_name))
            binding.btnProceed.isActivated = false
            return false
        }

        if (lastname.isBlank()) {
            binding.tilLastName.putError(getString(R.string.enter_last_name))
            binding.btnProceed.isActivated = false
            return false
        }

        if (dob.isBlank()) {
            binding.tilDob.putError(getString(R.string.select_your_dob))
            binding.btnProceed.isActivated = false
            return false
        }

        if (nationality.isBlank()) {
            binding.tilNationality.putError(getString(R.string.select_your_nationality))
            binding.btnProceed.isActivated = false
            return false
        }

        if (id.isBlank()) {
            binding.tilId.putError(getString(R.string.enter_id_number))
            binding.btnProceed.isActivated = false
            return false
        }

        binding.btnProceed.isActivated = true
        return true
    }

    override fun hideKeyboard() {
        binding.btnProceed.hideKeyboard()
    }

    private val observerIdVerification = Observer<ResponseStatus<IdVerificationResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val playerInfo = PlayerInfo.getLoginData()
                if (playerInfo != null) {
                    playerInfo.ramPlayerInfo?.idVerified = "Verified"
                    PlayerInfo.setLoginData(this, playerInfo)
                    startActivity(Intent(this, DepositActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                } else {
                    ErrorSheet(getString(R.string.deposit_error), getString(R.string.something_went_wrong), getString(R.string.close)) { onBackPressed() }.apply {
                        show(supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = getResponseMessage(this, RAM, it.errorCode)
                ErrorSheet(getString(R.string.deposit_error), errorMessage, getString(R.string.close)) {  }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.deposit_error), getString(it.errorMessageCode), getString(R.string.close)) {  }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerCountryList = Observer<ResponseStatus<CountryListResponse>> {
        when (it) {
            is ResponseStatus.Success -> operateCountryListResponse(it.response)

            is ResponseStatus.Error -> {
                val errorMessage = getResponseMessage(this, RAM, it.errorCode)
                ErrorSheet(getString(R.string.deposit_error), errorMessage, getString(R.string.close)) { onBackPressed() }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.deposit_error), getString(it.errorMessageCode), getString(R.string.close)) { onBackPressed() }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun operateCountryListResponse(response: CountryListResponse) {
        response.data?.let { list: ArrayList<CountryListResponse.Data?> ->
            when {
                list.isEmpty() -> {
                    ErrorSheet(getString(R.string.deposit_error), getString(R.string.data_not_received_001), getString(R.string.close)) { onBackPressed() }.apply {
                        show(supportFragmentManager, ErrorSheet.TAG)
                    }
                }
                list.size == 1 -> {
                    mCountryList = list
                    binding.eTNationality.setText(mCountryList[0]?.countryName)
                    binding.eTNationality.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    isNationalityClickAllowed = false
                }
                else -> {
                    mCountryList = list
                    isNationalityClickAllowed = true
                }
            }
        } ?: run {
            ErrorSheet(getString(R.string.deposit_error), getString(R.string.data_not_received_002), getString(R.string.close)) { onBackPressed() }.apply {
                show(supportFragmentManager, ErrorSheet.TAG)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun setToolbarElements() {
        binding.toolbar.llToolbarNotification.visibility    = View.VISIBLE
        binding.toolbar.tvBalance.text                      = HtmlCompat.fromHtml(PlayerInfo.getPlayerTotalBalanceBold(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.toolbar.tvToolbarLabel.text                 = getString(R.string.deposit)
        binding.toolbar.tvToolbarLabel.visibility           = View.VISIBLE
        binding.toolbar.ivToolBarIcon.visibility            = View.GONE

        if (PlayerInfo.getBadgeCount(this) > 0) {
            binding.toolbar.tvBadgeCount.text       = PlayerInfo.getBadgeCount(this).toString()
            binding.toolbar.tvBadgeCount.visibility = View.VISIBLE
        } else
            binding.toolbar.tvBadgeCount.visibility = View.GONE
    }

    override fun onHeaderInfoApiResponseCallback() {
        setToolbarElements()
    }
}