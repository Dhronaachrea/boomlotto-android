package com.skilrock.boomlotto.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentAddAccountOtpNonFirstTimeBinding
import com.skilrock.boomlotto.dialogs.AddBankSuccessSheet
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.request.AddNewAccountRequest
import com.skilrock.boomlotto.models.response.AddNewAccountResponse
import com.skilrock.boomlotto.models.response.BankDocUploadResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.AddBankViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AddBankOtpNonFirstTimeFragment : BaseFragment()  {

    private lateinit var binding: FragmentAddAccountOtpNonFirstTimeBinding
    private lateinit var viewModel: AddBankViewModel
    private lateinit var mAddAccountRequestData: AddNewAccountRequest
    private lateinit var mFilePathList: ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_account_otp_non_first_time, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        receiveParametersFromPreviousFragment()
        animateImage()
        setListeners()
        setOtpFocusListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[AddBankViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
        viewModel.liveDataAddNewAccResponse.observe(master, observerAddAccount)
        viewModel.liveDataOtpResponse.observe(master, observerAddAccountOtp)
        viewModel.liveDataBankDocResponse.observe(master, observerDoc)
    }

    private fun receiveParametersFromPreviousFragment() {
        this.arguments?.let {
            val requestData: AddNewAccountRequest?  = it.getParcelable("requestData")
            val filePathList: ArrayList<String>?    = it.getStringArrayList("filePathList")

            if (requestData != null && filePathList != null && filePathList.isNotEmpty()) {
                mAddAccountRequestData  = requestData
                mFilePathList           = filePathList
                Log.v("log", "File Path: $mFilePathList")
            } else {
                master.showToast(getString(R.string.some_internal_error))
                master.supportFragmentManager.popBackStack()
            }
        } ?: run {
            master.showToast(getString(R.string.some_problem_occurred))
            master.supportFragmentManager.popBackStack()
        }
    }

    private fun animateImage() {
        binding.ivLogo.animate().setDuration(1).scaleX(0f).scaleY(0f)
        binding.ivLogo.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            delay(400)
            binding.ivLogo.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(400)
                .withEndAction {}
        }
    }

    private fun setListeners() {
        val listEditText: List<AppCompatEditText> = listOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        binding.etOtp1.addTextChangedListener(OtpTextWatcher(binding.etOtp1, listEditText) { _, _ -> binding.tvError.text = "" })
        binding.etOtp2.addTextChangedListener(OtpTextWatcher(binding.etOtp2, listEditText) { _, _ -> binding.tvError.text = "" })
        binding.etOtp3.addTextChangedListener(OtpTextWatcher(binding.etOtp3, listEditText) { _, _ -> binding.tvError.text = "" })
        binding.etOtp4.addTextChangedListener(OtpTextWatcher(binding.etOtp4, listEditText) { _, _ -> binding.tvError.text = "" })

        binding.btnWithdraw.setOnClickListener {
            hideKeyboard()
            if (validateOtp()) {
                val otp     = binding.etOtp1.getTrimText() + binding.etOtp2.getTrimText() + binding.etOtp3.getTrimText() + binding.etOtp4.getTrimText()
                val copy    = mAddAccountRequestData.copy(verifyOtp = otp)
                viewModel.callOtpApi(copy, false)
            }
        }

        binding.tvResend.setOnClickListener {
            hideKeyboard()
            viewModel.callAddNewAccountApi(mAddAccountRequestData)
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

    private fun validateOtp() : Boolean {
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

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.add_bank), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    override fun hideKeyboard() {
        binding.btnWithdraw.hideKeyboard()
    }

    private val observerDoc = Observer<ResponseStatus<BankDocUploadResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                AddBankSuccessSheet {
                    master.supportFragmentManager.popBackStack()
                    master.supportFragmentManager.popBackStack()
                }.apply {
                    show(master.supportFragmentManager, AddBankSuccessSheet.TAG)
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, RAM, it.errorCode)
                ErrorSheet(getString(R.string.add_bank_error), errorMessage, getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.add_bank_error), getString(it.errorMessageCode), getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerAddAccountOtp = Observer<ResponseStatus<AddNewAccountResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val response: AddNewAccountResponse = it.response
                viewModel.callPlayerDocUploadApi(mAddAccountRequestData.accNum, mFilePathList, response.isWithdrawalApiCallRequired ?: false)
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, CASHIER, it.errorCode)
                ErrorSheet(getString(R.string.add_bank_error), errorMessage, getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.add_bank_error), getString(it.errorMessageCode), getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerAddAccount = Observer<ResponseStatus<AddNewAccountResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                master.showToast(getString(R.string.otp_sent_again))
            }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, CASHIER, it.errorCode)
                ErrorSheet(getString(R.string.add_bank_error), errorMessage, getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.add_bank_error), getString(it.errorMessageCode), getString(R.string.close)) {  }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
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
}