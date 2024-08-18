package com.skilrock.boomlotto.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentUpdateAddressBinding
import com.skilrock.boomlotto.dialogs.*
import com.skilrock.boomlotto.models.response.*
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.UpdateAddressViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateAddressFragment : BaseFragment() {

    private lateinit var binding: FragmentUpdateAddressBinding
    private lateinit var viewModel: UpdateAddressViewModel
    private lateinit var mCountryList: ArrayList<CountryListResponse.Data?>
    private lateinit var mStateList: ArrayList<StateResponse.Data?>
    private lateinit var mCityList: ArrayList<CityResponse.Data?>
    private var isStateClickAllowed: Boolean = true
    private var isCityClickAllowed: Boolean = true
    private var isNationalityClickAllowed: Boolean = true
    private var isSuccessSheetAllowed: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_address, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        callCountryListApi()
        setToolbarElements()
        setListeners()
        callPlayerProfileApi()
    }

    private fun setViewModel() {
        viewModel   = ViewModelProvider(master)[UpdateAddressViewModel::class.java]
        binding.lifecycleOwner = master

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataCountry.observe(master, observerCountryList)
        viewModel.liveDataIdVerification.observe(master, observerIdVerification)
        viewModel.liveDataState.observe(master, observerStateList)
        viewModel.liveDataCity.observe(master, observerCityList)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
        viewModel.liveDataForPlayerProfile.observe(master, observerProfileInfoResponse)
    }

    private fun callCountryListApi() {
        viewModel.callCountryListApi()
    }

    private fun callPlayerProfileApi() {
        viewModel.callPlayerProfileApi()
    }

    private fun setListeners() {
        binding.etCountry.setOnClickListener {
            hideKeyboard()
            if (this::mCountryList.isInitialized && isNationalityClickAllowed) {
                CountrySelectionSheet(mCountryList, false, ::onCountrySelected).apply {
                    show(master.supportFragmentManager, CountrySelectionSheet.TAG)
                }
            }
        }

        binding.etState.setOnClickListener {
            hideKeyboard()
            if (this::mStateList.isInitialized && isStateClickAllowed) {
                StateSelectionSheet(mStateList, ::onStateSelected).apply {
                    show(master.supportFragmentManager, StateSelectionSheet.TAG)
                }
            }
        }

        binding.etCity.setOnClickListener {
            hideKeyboard()
            if (this::mCityList.isInitialized && isCityClickAllowed) {
                CitySelectionSheet(mCityList, ::onCitySelected).apply {
                    show(master.supportFragmentManager, CitySelectionSheet.TAG)
                }
            }
        }

        binding.btnProceed.setOnClickListener {
            if (validateOnButtonClick()) {
                isSuccessSheetAllowed = true
                viewModel.callIdVerificationApi(binding.etAddressLine1.getTrimText(), binding.etAddressLine2.getTrimText(),
                    binding.etCountry.getTrimText(), binding.etState.tag.toString().trim(), binding.etCity.getTrimText(), binding.etTown.getTrimText(), binding.etZipCode.getTrimText())
            }
        }
    }

    private fun validateOnButtonClick() : Boolean {
        val addressLine1    = binding.etAddressLine1.getTrimText()
        val addressLine2    = binding.etAddressLine2.getTrimText()
        val town            = binding.etTown.getTrimText()
        val zipCode         = binding.etZipCode.getTrimText()
        val country         = binding.etCountry.getTrimText()
        val state           = binding.etState.getTrimText()
        val city            = binding.etCity.getTrimText()

        if (addressLine1.isBlank()) {
            binding.tilAddressLine1.putError(getString(R.string.enter_address_Line1))
            return false
        }

        if (addressLine2.isBlank()) {
            binding.tilAddressLine2.putError(getString(R.string.enter_address_Line2))
            return false
        }

        if (town.isBlank()) {
            binding.tilTown.putError(getString(R.string.enter_town))
            return false
        }

        if (zipCode.isBlank()) {
            binding.tilZipCode.putError(getString(R.string.enter_zipcode))
            return false
        }

        if (country.isBlank()) {
            binding.tilCountry.putError(getString(R.string.select_country))
            return false
        }

        if (state.isBlank()) {
            binding.tilState.putError(getString(R.string.enter_state))
            return false
        }

        if (city.isBlank()) {
            binding.tilCity.putError(getString(R.string.enter_city))
            return false
        }

        return true
    }

    private val observerIdVerification = Observer<ResponseStatus<IdVerificationResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                if (isSuccessSheetAllowed) {
                    isSuccessSheetAllowed = false
                    AddBankSuccessSheet {
                        master.supportFragmentManager.popBackStack()
                        master.supportFragmentManager.popBackStack()
                    }.apply {
                        show(master.supportFragmentManager, AddBankSuccessSheet.TAG)
                    }
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
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

    private val observerCountryList = Observer<ResponseStatus<CountryListResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                operateCountryListResponse(it.response)
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
                ErrorSheet(getString(R.string.withdrawal_error), errorMessage, getString(R.string.close)) { master.supportFragmentManager.popBackStack() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.withdrawal_error), getString(it.errorMessageCode), getString(R.string.close)) { master.supportFragmentManager.popBackStack() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerCityList = Observer<ResponseStatus<ArrayList<CityResponse.Data?>>> {
        when (it) {
            is ResponseStatus.Success -> {
                val cityList = it.response
                if (cityList.size == 1) {
                    mCityList = cityList
                    binding.etCity.setText(mCityList[0]?.cityName)
                    binding.etCity.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    isCityClickAllowed = false
                } else {
                    mCityList = cityList
                    isCityClickAllowed = true
                    binding.etCity.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow_light, 0)
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
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

    private val observerStateList = Observer<ResponseStatus<ArrayList<StateResponse.Data?>>> {
        when (it) {
            is ResponseStatus.Success -> {
                val stateList = it.response
                if (stateList.size == 1) {
                    mStateList = stateList
                    binding.etState.setText(mStateList[0]?.stateName)
                    binding.etState.tag = mStateList[0]?.stateCode
                    binding.etState.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    isStateClickAllowed = false
                    binding.etCity.setText("")
                    mStateList[0]?.stateCode?.let { stateCode -> viewModel.callCityListApi(stateCode) }
                } else {
                    mStateList = stateList
                    isStateClickAllowed = true
                    binding.etState.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow_light, 0)
                    binding.etCity.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow_light, 0)
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
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

    private fun setAvailableParameters(response: ProfileInfoForPlayerResponse) {
        response.ramAddressInfo?.let { it ->
            binding.etCountry.setText(it.country ?: "")
            binding.etCity.setText(it.city ?: "")

            if (it.addressOne is String && it.addressTwo is String) {
                binding.etAddressLine1.setText(it.addressOne.toString())
                binding.etAddressLine2.setText(it.addressTwo)
                binding.etTown.setText(it.town.toString())
                binding.etZipCode.setText(it.zip.toString())
            }

            val state       = it.state ?: ""
            val stateCode   = it.stateCode ?: ""

            if (state.isNotEmpty() && stateCode.isNotEmpty()) {
                binding.etState.setText(state)
                binding.etState.tag = stateCode
            } else {
                binding.etState.setText("")
                binding.etState.tag = ""
            }
        }
    }

    private val observerProfileInfoResponse = Observer<ResponseStatus<ProfileInfoForPlayerResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                if (it.response.errorCode == 0) {
                    setAvailableParameters(it.response)
                }
            }
            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, WEAVER, it.errorCode)
                ErrorSheet(getString(R.string.login_error), errorMessage, master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.login_error), getString(it.errorMessageCode), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun operateCountryListResponse(response: CountryListResponse) {
        response.data?.let { list: ArrayList<CountryListResponse.Data?> ->
            when {
                list.isEmpty() -> {
                    ErrorSheet(getString(R.string.withdrawal_error), getString(R.string.data_not_received_001), getString(R.string.close)) { master.supportFragmentManager.popBackStack() }.apply {
                        show(master.supportFragmentManager, ErrorSheet.TAG)
                    }
                }
                list.size == 1 -> {
                    mCountryList = list
                    binding.etCountry.setText(mCountryList[0]?.countryName)
                    binding.etCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    isNationalityClickAllowed = false
                    binding.etState.setText("")
                    binding.etState.tag = ""
                    binding.etCity.setText("")
                    mCountryList[0]?.countryCode?.let { countryCode -> viewModel.callStateListApi(countryCode) }
                }
                else -> {
                    mCountryList = list
                    isNationalityClickAllowed = true
                }
            }
        } ?: run {
            ErrorSheet(getString(R.string.withdrawal_error), getString(R.string.data_not_received_002), getString(R.string.close)) { master.supportFragmentManager.popBackStack() }.apply {
                show(master.supportFragmentManager, ErrorSheet.TAG)
            }
        }
    }

    private fun onCountrySelected(country: CountryListResponse.Data?, isNationality: Boolean) {
        country?.countryCode?.let {
            lifecycleScope.launch {
                delay(300)
                if (isNationality) {
                    binding.etCountry.setText(country.countryName)
                    animateTextInputLayout(binding.tilCountry)
                } else {
                    binding.etCountry.setText(country.countryName)
                    animateTextInputLayout(binding.tilCountry)
                    binding.etState.setText("")
                    binding.etState.tag = ""
                    binding.etCity.setText("")
                    viewModel.callStateListApi(countryCode = country.countryCode)
                }
            }
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

    private fun onStateSelected(state: StateResponse.Data) {
        state.stateCode?.let {
            lifecycleScope.launch {
                delay(300)
                binding.etState.setText(state.stateName)
                binding.etState.tag = state.stateCode
                animateTextInputLayout(binding.tilState)
            }
            binding.etCity.setText("")
            viewModel.callCityListApi(stateCode = state.stateCode)
        }
    }

    private fun onCitySelected(city: CityResponse.Data) {
        lifecycleScope.launch {
            delay(300)
            binding.etCity.setText(city.cityName)
            animateTextInputLayout(binding.tilCity)
        }
    }

    override fun hideKeyboard() {
        binding.btnProceed.hideKeyboard()
    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.update_address), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }
}