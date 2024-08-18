package com.skilrock.boomlotto.ui.fragment

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.contracts.DocumentContract
import com.skilrock.boomlotto.contracts.DocumentPdfSelect
import com.skilrock.boomlotto.databinding.FragmentUpdateProfileBinding
import com.skilrock.boomlotto.dialogs.*
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.IdVerificationResponse
import com.skilrock.boomlotto.models.response.ProfileInfoForPlayerResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.UpdateProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class UpdateProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var viewModel: UpdateProfileViewModel
    private lateinit var mCountryList: ArrayList<CountryListResponse.Data?>
    private var isNationalityClickAllowed: Boolean = true
    private var mDocName = "PASSPORT"
    private var mUriDocumentList: ArrayList<Uri> = ArrayList()
    private var isSuccessSheetAllowed: Boolean = false
    private lateinit var mPlayerResponseData: ProfileInfoForPlayerResponse

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        setListeners()
        callCountryListApi()
        Log.d("TAg", "<--------------------------000000000000000000-------------------->")
        receiveParametersFromPreviousFragment()
        setAvailableParameters()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(master)[UpdateProfileViewModel::class.java]
        binding.lifecycleOwner = master
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataCountry.observe(master, observerCountryList)
        viewModel.liveDataIdVerification.observe(master, observerIdVerification)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun receiveParametersFromPreviousFragment() {
        this.arguments?.let {
            val responseData: ProfileInfoForPlayerResponse?  = it.getParcelable("profilePlayerResponse")

            if (responseData != null ) {
                mPlayerResponseData  = responseData
            } else {

                master.showToast(getString(R.string.some_internal_error))
                master.supportFragmentManager.popBackStack()
            }
        } ?: run {
            master.showToast(getString(R.string.some_problem_occurred))
            master.supportFragmentManager.popBackStack()
        }
    }

    private fun setAvailableParameters() {
        mPlayerResponseData.playerInfoBean?.run {
            binding.etFirstName.setText(firstName ?: "")
            binding.etLastName.setText(lastName ?: "")
            binding.eTDob.setText(dob ?: "")
            binding.eTNationality.setText(country ?: "")
        }

        val latestDocument = mPlayerResponseData.latestDocuments?.find { it?.docType == "ID_PROOF"}
        if (latestDocument != null) {
            binding.eTId.setText(latestDocument.documentValue)
            binding.etIdExpiry.setText(getFormattedDashDate(latestDocument.validUpTo.toString()))
            when (latestDocument.docName) {
                "PASSPORT" -> {
                    binding.tvPassportId.background = ContextCompat.getDrawable(master, R.drawable.id_number_selected)
                    binding.tvEmiratesId.background = ContextCompat.getDrawable(master, R.drawable.id_number_unselected)
                    binding.tvPassportId.setTextColor(ContextCompat.getColor(master, R.color.color_app_pink))
                    binding.tvEmiratesId.setTextColor(ContextCompat.getColor(master, R.color.color_app_base_blue))
                    mDocName = "PASSPORT"
                }

                "EMIRATES_ID" -> {
                    binding.tvPassportId.background = ContextCompat.getDrawable(master, R.drawable.id_number_unselected)
                    binding.tvEmiratesId.background = ContextCompat.getDrawable(master, R.drawable.id_number_selected)
                    binding.tvPassportId.setTextColor(ContextCompat.getColor(master, R.color.color_app_base_blue))
                    binding.tvEmiratesId.setTextColor(ContextCompat.getColor(master, R.color.color_app_pink))
                    mDocName = "EMIRATES_ID"
                }

                else -> {
                    binding.tvPassportId.background = ContextCompat.getDrawable(master, R.drawable.id_number_selected)
                    binding.tvEmiratesId.background = ContextCompat.getDrawable(master, R.drawable.id_number_unselected)

                    binding.tvPassportId.setTextColor(ContextCompat.getColor(master, R.color.color_app_pink))
                    binding.tvEmiratesId.setTextColor(ContextCompat.getColor(master, R.color.color_app_base_blue))

                    binding.eTId.hint = getString(R.string.enter_passport_id_number)
                    mDocName = "PASSPORT"
                    binding.tvIdExpiry.text = getString(R.string.expiry_date_of_passport)
                }
            }

        } else {
            binding.eTId.setText("")
            binding.etIdExpiry.setText("")
        }
    }

    private fun callCountryListApi() {
        viewModel.callCountryListApi()
    }

    private fun setListeners() {
        binding.eTDob.setOnClickListener {
            hideKeyboard()
            openDobDialog(master, ::onDateOfBirthSelected)
        }

        binding.etIdExpiry.setOnClickListener {
            hideKeyboard()
            openIdExpiryDialog(master) { date ->
                lifecycleScope.launch {
                    delay(300)
                    binding.etIdExpiry.setText(date)
                    animateTextInputLayout(binding.tilIdExpiry)
                }
            }
        }

        binding.eTNationality.setOnClickListener {
            hideKeyboard()
            if (this::mCountryList.isInitialized && isNationalityClickAllowed) {
                CountrySelectionSheet(mCountryList, true, ::onCountrySelected).apply {
                    show(master.supportFragmentManager, CountrySelectionSheet.TAG)
                }
            }
        }

        binding.eTId.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    hideKeyboard()
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
            binding.tvIdExpiry.text = getString(R.string.expiry_date_of_passport)
        }

        binding.tvEmiratesId.setOnClickListener {
            binding.tvEmiratesId.background = ContextCompat.getDrawable(master, R.drawable.id_number_selected)
            binding.tvPassportId.background = ContextCompat.getDrawable(master, R.drawable.id_number_unselected)

            binding.tvEmiratesId.setTextColor(ContextCompat.getColor(master, R.color.color_app_pink))
            binding.tvPassportId.setTextColor(ContextCompat.getColor(master, R.color.color_app_base_blue))

            binding.eTId.hint = getString(R.string.enter_emirates_id_number)
            mDocName = "EMIRATES_ID"
            binding.tvIdExpiry.text = getString(R.string.expiry_date_of_emirates_id)
        }

        binding.btnProceed.setOnClickListener {
            binding.btnProceed.hideKeyboard()
            if (validateOnButtonClick()) {
                val listDocPath = ArrayList<String>()
                mUriDocumentList.forEach { uri ->
                    URIPathHelper().getPath(master, uri)?.let { path ->
                        listDocPath.add(path)
                    }
                }

                if (listDocPath.isNotEmpty()) {
                    isSuccessSheetAllowed = true
                    viewModel.callIdVerificationWithdrawalApi(binding.etFirstName.getTrimText(), binding.etLastName.getTrimText(), binding.eTDob.getTrimText(),
                        binding.eTNationality.getTrimText(), mDocName, binding.eTId.getTrimText(), binding.etIdExpiry.getTrimText(), listDocPath)
                } else
                    master.showToast(getString(R.string.some_technical_issue))
            }
        }

        binding.cardSelectAttachment.setOnClickListener {
            if ( mUriDocumentList.size < 5) {
                AddBankFileSelectingSheet(::onUpdateProfileFileSelectingSheetCallback).apply {
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

    private fun onUpdateProfileFileSelectingSheetCallback(userResponse : String) {
        if (userResponse == "gallery")
            openDocumentSelector()
        else if (userResponse == "document")
            openDocumentSelectorForPdf()
    }

    private fun onDateOfBirthSelected(strDate: String) {
        lifecycleScope.launch {
            delay(300)
            binding.eTDob.setText(strDate)
            animateTextInputLayout(binding.tilDob)
        }
    }

    private fun onCountrySelected(country: CountryListResponse.Data?, isNationality: Boolean) {
        country?.countryCode?.let {
            lifecycleScope.launch {
                delay(300)
                if (isNationality) {
                    binding.eTNationality.setText(country.countryName)
                    animateTextInputLayout(binding.tilNationality)
                } else {
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

    private fun validateOnButtonClick() : Boolean {
        val firstName       = binding.etFirstName.getTrimText()
        val lastname        = binding.etLastName.getTrimText()
        val dob             = binding.eTDob.getTrimText()
        val nationality     = binding.eTNationality.getTrimText()
        val id              = binding.eTId.getTrimText()
        val expiry          = binding.etIdExpiry.getTrimText()

        if (firstName.isBlank()) {
            binding.tilFirstName.putError(getString(R.string.enter_first_name))
            return false
        }

        if (lastname.isBlank()) {
            binding.tilLastName.putError(getString(R.string.enter_last_name))
            return false
        }

        if (dob.isBlank()) {
            binding.tilDob.putError(getString(R.string.select_your_dob))
            return false
        }

        if (nationality.isBlank()) {
            binding.tilNationality.putError(getString(R.string.select_your_nationality))
            return false
        }

        if (id.isBlank()) {
            binding.tilId.putError(getString(R.string.enter_id_number))
            return false
        }

        if (expiry.isBlank()) {
            binding.tilIdExpiry.putError(getString(R.string.select_expiry_date))
            return false
        }

        if (mUriDocumentList.isEmpty()) {
            master.showToast(getString(R.string.please_select_id_proof))
            binding.cardSelectAttachment.startAnimation(shakeError())
            return false
        }

        return true
    }

    private val observerIdVerification = Observer<ResponseStatus<IdVerificationResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                if (isSuccessSheetAllowed) {
                    isSuccessSheetAllowed = false
                    PlayerInfo.setPlayerFirstName(master, binding.etFirstName.getTrimText())
                    PlayerInfo.setPlayerLastName(master, binding.etLastName.getTrimText())
                    AddBankSuccessSheet(::onSheetClose).apply {
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

    private fun onSheetClose() {
        val fm: FragmentManager = master.supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        if (binding.etFirstName.getTrimText().isNotEmpty()) {
            master.updateDrawerPlayerName(binding.etFirstName.getTrimText())
        }
        master.openFragment(MyAccountFragment(), "MyAccountFragment", true, isFadeAnimation = true)
    }

    override fun hideKeyboard() {
        binding.btnProceed.hideKeyboard()
    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.update_profile), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
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

    private val observerCountryList = Observer<ResponseStatus<CountryListResponse>> {
        when (it) {
            is ResponseStatus.Success -> operateCountryListResponse(it.response)

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
                    binding.eTNationality.setText(mCountryList[0]?.countryName)
                    binding.eTNationality.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    isNationalityClickAllowed = false
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

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDashDate(Date: String) : String {
        return try {
            val parser      = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd, yyyy")
            parser.parse(Date)?.let { formatter.format(it) } ?: "-"
        } catch (e: Exception) {
            "-"
        }
    }
}