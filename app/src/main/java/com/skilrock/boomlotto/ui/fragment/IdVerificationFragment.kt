package com.skilrock.boomlotto.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentIdVerificationBinding
import com.skilrock.boomlotto.dialogs.CountrySelectionSheet
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.IdVerificationResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.IdVerificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IdVerificationFragment : BaseFragment() {

    private lateinit var binding: FragmentIdVerificationBinding
    private lateinit var viewModel: IdVerificationViewModel
    private lateinit var mCountryList: ArrayList<CountryListResponse.Data?>
    private var isNationalityClickAllowed: Boolean = true
    private var mDocName = "PASSPORT"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_id_verification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        callCountryListApi()
        setOnClickListeners()
        setTextWatchers()
    }

    override fun onResume() {
        super.onResume()
        setToolbarElements()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[IdVerificationViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataHideKeyboard.observe(master, observerHideKeyboard)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataLoginCountry.observe(master, observerCountryList)
        viewModel.liveDataIdVerification.observe(master, observerIdVerification)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun callCountryListApi() {
        viewModel.callCountryListApi()
    }

    private fun setOnClickListeners() {
        binding.btnProceed.isActivated = false

        binding.eTDob.setOnClickListener {
            hideKeyboard()
            openDobDialog(master, ::onDateOfBirthSelected)
        }

        binding.eTNationality.setOnClickListener {
            hideKeyboard()
            if (isNationalityClickAllowed) {
                CountrySelectionSheet(mCountryList, true, ::onCountryClick).apply {
                    show(master.supportFragmentManager, CountrySelectionSheet.TAG)
                }
            }
        }

        binding.eTId.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    binding.btnProceed.performClick()
                    true
                }
                else -> false
            }
        }

        binding.tvPassportId.setOnClickListener {
            binding.tvPassportId.background = ContextCompat.getDrawable(master, R.drawable.id_number_selected)
            binding.tvEmiratesId.background = ContextCompat.getDrawable(master, R.drawable.id_number_unselected)

            binding.tvPassportId.setTextColor(ContextCompat.getColor(master, R.color.color_app_pink))
            binding.tvEmiratesId.setTextColor(ContextCompat.getColor(master, R.color.color_app_base_blue))

            binding.eTId.hint = getString(R.string.enter_passport_id_number)
            mDocName = "PASSPORT"
        }

        binding.tvEmiratesId.setOnClickListener {
            binding.tvEmiratesId.background = ContextCompat.getDrawable(master, R.drawable.id_number_selected)
            binding.tvPassportId.background = ContextCompat.getDrawable(master, R.drawable.id_number_unselected)

            binding.tvEmiratesId.setTextColor(ContextCompat.getColor(master, R.color.color_app_pink))
            binding.tvPassportId.setTextColor(ContextCompat.getColor(master, R.color.color_app_base_blue))

            binding.eTId.hint = getString(R.string.enter_emirates_id_number)
            mDocName = "EMIRATES_ID"
        }

        binding.btnProceed.setOnClickListener {
            if (validateOnButtonClick()) {
                viewModel.callIdVerificationApi(binding.etFirstName.getTrimText(), binding.etLastName.getTrimText(), binding.eTDob.getTrimText(), binding.eTNationality.getTrimText(), mDocName, binding.eTId.getTrimText())
            }
        }
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
                        til.animate().scaleX(1f).scaleY(1.2f).setDuration(300).withEndAction {
                            til.animate().scaleX(1f).scaleY(1f).setDuration(300).withEndAction {

                            }
                        }
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

    override fun setToolbarElements() {
        master.binding.tvToolbarTitle.visibility    = View.VISIBLE
        master.binding.ivToolBarIcon.visibility     = View.GONE
        master.binding.tvToolbarTitle.text          = getString(R.string.deposit)
    }

    private val observerIdVerification = Observer<ResponseStatus<IdVerificationResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val playerInfo = PlayerInfo.getLoginData()
                if (playerInfo != null) {
                    playerInfo.ramPlayerInfo?.idVerified = "Verified"
                    PlayerInfo.setLoginData(master, playerInfo)
                } else {
                    ErrorSheet(getString(R.string.deposit_error), getString(R.string.something_went_wrong), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                        show(master.supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
                ErrorSheet(getString(R.string.deposit_error), errorMessage, master.getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.deposit_error), getString(it.errorMessageCode), master.getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerCountryList = Observer<ResponseStatus<CountryListResponse>> {
        when (it) {
            is ResponseStatus.Success -> operateCountryListResponse(it.response)

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
                ErrorSheet(getString(R.string.deposit_error), errorMessage, master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.deposit_error), getString(it.errorMessageCode), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun operateCountryListResponse(response: CountryListResponse) {
        response.data?.let { list: ArrayList<CountryListResponse.Data?> ->
            when {
                list.isEmpty() -> {
                    ErrorSheet(getString(R.string.deposit_error), getString(R.string.data_not_received_001), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                        show(master.supportFragmentManager, ErrorSheet.TAG)
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
            ErrorSheet(getString(R.string.deposit_error), getString(R.string.data_not_received_002), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                show(master.supportFragmentManager, ErrorSheet.TAG)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        master.binding.tvToolbarTitle.visibility    = View.GONE
        master.binding.ivToolBarIcon.visibility     = View.VISIBLE
        master.binding.tvToolbarTitle.text          = ""
    }
}