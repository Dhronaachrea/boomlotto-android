package com.skilrock.boomlotto.ui.fragment

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.ResultAdapter
import com.skilrock.boomlotto.contracts.DocumentContract
import com.skilrock.boomlotto.contracts.DocumentPdfSelect
import com.skilrock.boomlotto.databinding.FragmentAddBankBinding
import com.skilrock.boomlotto.dialogs.*
import com.skilrock.boomlotto.models.request.AddNewAccountRequest
import com.skilrock.boomlotto.models.response.AddNewAccountResponse
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.AddBankViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddBankFragment : BaseFragment() {

    private lateinit var binding: FragmentAddBankBinding
    private lateinit var viewModel: AddBankViewModel
    private lateinit var mAdapter: ResultAdapter
    private lateinit var mCountryList: ArrayList<CountryListResponse.Data?>
    private var isNationalityClickAllowed: Boolean = true
    private var isBankClickAllowed: Boolean = true
    private var mUriDocumentList: ArrayList<Uri> = ArrayList()
    private var mPaymentOptions: PaymentOptionsResponse? = null
    private lateinit var mPayTypeMapList: List<PaymentOptionsResponse.PayTypeMap>
    private lateinit var mPayTypeMap: PaymentOptionsResponse.PayTypeMap
    private lateinit var mAddAccountRequestData: AddNewAccountRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = ResultAdapter(master)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_bank, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements()
        setViewModel()
        setClickListeners()
        callCountryListApi()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[AddBankViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataCountry.observe(master, observerCountryList)
        viewModel.liveDataPaymentOptions.observe(master, observerPaymentOptions)
        viewModel.liveDataAddNewAccResponse.observe(master, observerAddAccount)
    }

    private fun callCountryListApi() {
        viewModel.callCountryListApi()
    }

    override fun hideKeyboard() {
        binding.btnSubmit.hideKeyboard()
    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.withdrawal), View.VISIBLE, View.GONE)
    }

    private fun setClickListeners() {
        binding.etCountry.setOnClickListener {
            hideKeyboard()
            if (this::mCountryList.isInitialized && isNationalityClickAllowed) {
                CountrySelectionSheet(mCountryList, false, ::onCountrySelected).apply {
                    show(master.supportFragmentManager, CountrySelectionSheet.TAG)
                }
            }
        }

        binding.cardSelectAttachment.setOnClickListener {
            if ( mUriDocumentList.size < 5) {
                AddBankFileSelectingSheet(::onAddBankFileSelectingSheetCallback).apply {
                    show(master.supportFragmentManager, AddBankFileSelectingSheet.TAG) }
            } else {
                master.showToast(getString(R.string.you_cannot_select_more_than_five_documents))
            }
        }

        binding.ivDeleteAttachmentForItem1.setOnClickListener {
            mUriDocumentList.removeAt(0)
            resetCardView()
            fillSelectedDocument(false)
        }

        binding.ivDeleteAttachmentForItem2.setOnClickListener {
            mUriDocumentList.removeAt(1)
            resetCardView()
            fillSelectedDocument(false)
        }

        binding.ivDeleteAttachmentForItem3.setOnClickListener {
            mUriDocumentList.removeAt(2)
            resetCardView()
            fillSelectedDocument(false)
        }

        binding.ivDeleteAttachmentForItem4.setOnClickListener {
            mUriDocumentList.removeAt(3)
            resetCardView()
            fillSelectedDocument(false)
        }

        binding.ivDeleteAttachmentForItem5.setOnClickListener {
            mUriDocumentList.removeAt(4)
            resetCardView()
            fillSelectedDocument(false)
        }

        binding.etBankName.setOnClickListener {
            hideKeyboard()
            if (isBankClickAllowed) {
                BankSelectionSheet(mPayTypeMapList, ::onBankSelected).apply {
                    show(master.supportFragmentManager, StateSelectionSheet.TAG)
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            hideKeyboard()
            if (validateOnButtonClick()) {
                mAddAccountRequestData = AddNewAccountRequest(
                    accHolderName = binding.etBeneficiaryName.getTrimText(), nickName = binding.etNickName.getTrimText(),
                    accNum = binding.etAccountNumber.getTrimText(), paymentTypeCode = mPayTypeMap.payTypeCode,
                    paymentTypeId = mPayTypeMap.payTypeId.toString(), subTypeId = mPayTypeMap.subTypeCode, currencyCode = PlayerInfo.getCurrency(), ifscCode = binding.etIfsc.getTrimText()
                )
                viewModel.callAddNewAccountApi(mAddAccountRequestData)
            }
        }
    }

    private fun onAddBankFileSelectingSheetCallback(userResponse : String) {
        if (userResponse == "gallery") {
            openDocumentSelector()
        } else if (userResponse == "document") {
            openDocumentSelectorForPdf()
        }
    }

    private fun resetCardView() {
        binding.tvAttachmentForItem1.text    = ""
        binding.cardAttachedItem1.visibility = View.GONE
        binding.tvAttachmentForItem2.text    = ""
        binding.cardAttachedItem2.visibility = View.GONE
        binding.tvAttachmentForItem3.text    = ""
        binding.cardAttachedItem3.visibility = View.GONE
        binding.tvAttachmentForItem4.text    = ""
        binding.cardAttachedItem4.visibility = View.GONE
        binding.tvAttachmentForItem5.text    = ""
        binding.cardAttachedItem5.visibility = View.GONE
    }

    private fun fillSelectedDocument(isAddOperation: Boolean) {
        val lastIndex = mUriDocumentList.size - 1
        mUriDocumentList.forEachIndexed { index, uri ->
            val filePath = URIPathHelper().getPath(master, uri)
            if (filePath != null && filePath.isNotEmpty()) {
                when (index) {
                    0 -> setDocumentView(binding.tvAttachmentForItem1, binding.cardAttachedItem1, filePath, index == lastIndex, isAddOperation, uri)
                    1 -> setDocumentView(binding.tvAttachmentForItem2, binding.cardAttachedItem2, filePath, index == lastIndex, isAddOperation, uri)
                    2 -> setDocumentView(binding.tvAttachmentForItem3, binding.cardAttachedItem3, filePath, index == lastIndex, isAddOperation, uri)
                    3 -> setDocumentView(binding.tvAttachmentForItem4, binding.cardAttachedItem4, filePath, index == lastIndex, isAddOperation, uri)
                    4 -> setDocumentView(binding.tvAttachmentForItem5, binding.cardAttachedItem5, filePath, index == lastIndex, isAddOperation, uri)
                }
            }
        }
    }

    private fun setDocumentView(textView: MaterialTextView, cardView: MaterialCardView, filePath: String, isAnimationAllow: Boolean, isAddOperation: Boolean, uri: Uri) {
        textView.text = filePath.substringAfterLast("/")
        cardView.visibility = View.VISIBLE

        cardView.setOnClickListener {
            AddBankPreviewSelectedFileSheet(uri,URIPathHelper().getPath(master, uri)).apply {
                show(master.supportFragmentManager, AddBankPreviewSelectedFileSheet.TAG) }
        }

        if (isAddOperation && isAnimationAllow) {
            val animateBackground = ValueAnimator.ofObject(
                ArgbEvaluator(),
                Color.parseColor("#dcdee0"),
                ContextCompat.getColor(master, R.color.fragment_background_color)
            )
            animateBackground.duration = 1000
            animateBackground.repeatCount = 2

            animateBackground.addUpdateListener { animator ->
                cardView.setCardBackgroundColor(animator.animatedValue as Int)
            }
            animateBackground.start()
        }
    }

    private fun onBankSelected(payTypeMap: PaymentOptionsResponse.PayTypeMap) {
        lifecycleScope.launch {
            delay(300)
            mPayTypeMap = payTypeMap
            binding.etBankName.setText(payTypeMap.subTypeValue)
            binding.etBankName.tag = payTypeMap.subTypeCode
            animateTextInputLayout(binding.tilBankName)
        }
    }

    private fun openDocumentSelectorForPdf() {
        activityResultPdfSelect.launch(Unit)
    }

    private val activityResultPdfSelect = registerForActivityResult(DocumentPdfSelect()) {
        it?.let { uri: Uri ->
            // work on later
            //resetCardView()
            //mUriDocumentList.add(uri)
            //fillSelectedDocument(true)
        }
    }

    private fun openDocumentSelector() {
        val permissionCheck = ContextCompat.checkSelfPermission(master, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(master, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            activityResultContract.launch(Unit)
        }
    }

    private val activityResultContract = registerForActivityResult(DocumentContract()) {
        it?.let { uri: Uri ->

            resetCardView()
            mUriDocumentList.add(uri)
            fillSelectedDocument(true)
        }
    }

    private fun onCountrySelected(country: CountryListResponse.Data?, isNationality: Boolean) {
        Log.d("log", "Is Nationality: $isNationality")
        country?.countryCode?.let {
            lifecycleScope.launch {
                delay(300)
                binding.etCountry.setText(country.countryName)
                animateTextInputLayout(binding.tilCountry)
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

    private val observerCountryList = Observer<ResponseStatus<CountryListResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                operateCountryListResponse(it.response)
                viewModel.callPaymentOptionsApi()
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

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    private val observerPaymentOptions = Observer<ResponseStatus<JSONObject>> { responseStatus ->
        when (responseStatus) {
            is ResponseStatus.Success -> {
                mPaymentOptions = PaymentOptionsResponseParser().handleResponse(responseStatus.response)
                handlePaymentOptionsResponseForWithdraw()
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

    private fun handlePaymentOptionsResponseForWithdraw() {
        mPaymentOptions?.listPayTypeMap?.let { list: ArrayList<PaymentOptionsResponse.PayTypeMap> ->
            if (list.isNotEmpty()) {
                //val payTypeMapList: List<PaymentOptionsResponse.PayTypeMap> = list.filter { map: PaymentOptionsResponse.PayTypeMap -> map.payTypeCode == "BANK_TRANS" }
                mPayTypeMapList = list
                if (list.size == 1) {
                    isBankClickAllowed = false
                    mPayTypeMap = list[0]
                    binding.etBankName.setText(list[0].subTypeValue)
                    binding.etBankName.tag = list[0].subTypeValue
                    binding.etBankName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                } else {
                    isBankClickAllowed = true
                }
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

    private fun validateOnButtonClick() : Boolean {
        val beneficiaryName = binding.etBeneficiaryName.getTrimText()
        val nickName        = binding.etNickName.getTrimText()
        val country         = binding.etCountry.getTrimText()
        val bankName        = binding.etBankName.getTrimText()
        val ifsc            = binding.etIfsc.getTrimText()
        val accountNumber   = binding.etAccountNumber.getTrimText()

        if (beneficiaryName.isBlank()) {
            binding.tilBeneficiaryName.putError(getString(R.string.enter_beneficiary_name))
            return false
        }

        if (nickName.isBlank()) {
            binding.tilNickName.putError(getString(R.string.enter_your_nick_name))
            return false
        }

        if (country.isBlank()) {
            binding.tilCountry.putError(getString(R.string.select_country))
            return false
        }

        if (bankName.isBlank()) {
            binding.tilBankName.putError(getString(R.string.select_bank_name))
            return false
        }

        if (ifsc.isBlank()) {
            binding.tilIfsc.putError(getString(R.string.enter_code))
            return false
        }

        if (accountNumber.isBlank()) {
            binding.tilAccountNumber.putError(getString(R.string.enter_iban_account_number))
            return false
        }

        if (mUriDocumentList.isEmpty()) {
            master.showToast(getString(R.string.please_select_document_to_upload))
            binding.cardSelectAttachment.startAnimation(shakeError())
            return false
        }

        if (!this::mPayTypeMap.isInitialized) {
            master.showToast(getString(R.string.some_problem_occurred))
            return false
        }

        return true
    }

    private val observerAddAccount = Observer<ResponseStatus<AddNewAccountResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                if (this::mAddAccountRequestData.isInitialized) {
                    val verificationCode = it.response.verificationCode
                    if (BuildConfig.BUILD_TYPE == BUILD_TYPE_UAT && verificationCode != null && verificationCode.isNotBlank()) {
                        val text = "Your OTP to add account is ${verificationCode}. Don't share it with any one."
                        showNotification(master, text)
                    }

                    val listDocPath = ArrayList<String>()
                    mUriDocumentList.forEach { uri ->
                        URIPathHelper().getPath(master, uri)?.let { path ->
                            listDocPath.add(path)
                        }
                    }

                    if (listDocPath.isNotEmpty()) {
                        val bundleAmount = Bundle()
                        if (master.mFirstTimeWithdrawAmount > 0) {
                            bundleAmount.putDouble("amount", master.mFirstTimeWithdrawAmount)
                            bundleAmount.putParcelable("requestData", mAddAccountRequestData)
                            bundleAmount.putStringArrayList("filePathList", listDocPath)
                            master.openFragment(AddBankOtpFragment(), "AddBankOtpFragment", true, args = bundleAmount, popPreviousFragment = false)
                        } else {
                            bundleAmount.putParcelable("requestData", mAddAccountRequestData)
                            bundleAmount.putStringArrayList("filePathList", listDocPath)
                            master.openFragment(AddBankOtpNonFirstTimeFragment(), "AddBankOtpWithoutWithdrawFragment", true, args = bundleAmount, popPreviousFragment = false)
                        }
                    } else
                        master.showToast(getString(R.string.some_technical_issue))
                } else
                    master.showToast(getString(R.string.something_went_wrong))
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
}