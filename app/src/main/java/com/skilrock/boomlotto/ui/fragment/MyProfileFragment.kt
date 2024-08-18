package com.skilrock.boomlotto.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentMyProfileBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.request.PlayerProfileRequest
import com.skilrock.boomlotto.models.response.PlayerProfileResponse
import com.skilrock.boomlotto.models.response.ProfileInfoForPlayerResponse
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.utility.WEAVER
import com.skilrock.boomlotto.utility.showToast
import com.skilrock.boomlotto.viewmodels.MyProfileViewModel
import java.text.SimpleDateFormat

class MyProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var viewModel: MyProfileViewModel
    private var mProfileResponse: ProfileInfoForPlayerResponse? = null
    private var isBothContactPersonalDetailsVerified: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements()
        setViewModel()
        callPlayerProfileApi()
        setOnClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[MyProfileViewModel::class.java]
        binding.lifecycleOwner = this

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataForPlayerProfile.observe(master, observerProfileInfoResponse)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }
    private fun callPlayerProfileApi() {
        viewModel.callPlayerProfileApi()
    }

    private val observerProfileInfoResponse = Observer<ResponseStatus<ProfileInfoForPlayerResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                mProfileResponse = it.response
                initializeWidgets(it.response)
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

    @SuppressLint("SetTextI18n")
    private fun initializeWidgets(response: ProfileInfoForPlayerResponse) {
            response.playerInfoBean?.let {
                if (it.phoneVerified == "Y" && it.emailVerified == "Y") {
                    binding.cvPending.visibility                = View.GONE
                    binding.cvVerified.visibility               = View.VISIBLE
                    isBothContactPersonalDetailsVerified = true

                } else {
                    binding.cvVerified.visibility               = View.GONE
                    binding.cvPending.visibility                = View.VISIBLE
                    isBothContactPersonalDetailsVerified = false
                }

                if (it.phoneVerified == "Y") {
                    binding.tvMobileNumber.text     = it.mobileNumber
                    if (it.emailVerified == "Y") {
                        binding.tvEmailAddress.text = it.emailId
                    } else {
                        binding.tvEmailAddress.text = "-"
                    }
                } else {
                    binding.tvMobileNumber.text     = "-"
                }

                when {
                    (it.firstName?.isNotEmpty() == true) && (it.lastName?.isNotEmpty() == true ) -> {
                        val nameOfPlayer = "${it.firstName} ${it.lastName}"
                        binding.tvName.text = nameOfPlayer
                    }
                    (it.firstName?.isEmpty() == true) && (it.lastName?.isNotEmpty() == true ) -> {
                        binding.tvName.text = it.lastName
                    }
                    (it.firstName?.isNotEmpty() == true) && (it.lastName?.isEmpty() == true ) -> {
                        binding.tvName.text = it.firstName
                    }
                    else -> {
                        binding.tvName.text = "-"
                    }
                }

                it.dob?.let { dob -> binding.tvDateOfBirth.text = getFormattedSlashDate(dob) } ?: run { binding.tvDateOfBirth.text = "-" }
                binding.tvNationality.text = it.country ?: "-"
            }

            response.ramPlayerInfo?.let {
                if (it.idVerified.equals("VERIFIED", true)) {
                    binding.cvVerifiedPersonalDetail.visibility     = View.VISIBLE
                    binding.cvPendingPersonalDetail.visibility      = View.GONE
                    if (isBothContactPersonalDetailsVerified) {
                        binding.llProfileUpdateInfoCard.visibility  = View.GONE
                    } else {
                        binding.llProfileUpdateInfoCard.visibility  = View.VISIBLE
                    }
                } else {
                    binding.cvPendingPersonalDetail.visibility  = View.VISIBLE
                    binding.cvVerifiedPersonalDetail.visibility = View.GONE
                    binding.llProfileUpdateInfoCard.visibility  = View.VISIBLE

                }
                it.updatedAt?.let { profileUpdatedOn -> binding.tvProfileUpdatedOn.text = " " + getFormattedDate(profileUpdatedOn) } ?: run { binding.tvProfileUpdatedOn.text = "-"}
            }

            if (!response.latestDocuments.isNullOrEmpty()) {
                response.latestDocuments.forEach{ sublistInList: ProfileInfoForPlayerResponse.LatestDocument? ->
                    sublistInList?.run {
                        if(docType == "ID_PROOF") {
                            binding.tvProofOfId.text  = docType.replace("_"," ")
                            binding.tvIdNumber.text   = documentValue ?: "-"
                            binding.tvIdExpiry.text   = getFormattedDashDate(validUpTo.toString())
                        } else
                            setDefaultValue()
                    } ?: run {
                        setDefaultValue()
                    }
                }
            } else {
                setDefaultValue()
            }

            response.ramAddressInfo?.let {
                if (it.addressOne is String && it.addressTwo is String) {
                    val address = "${it.addressOne} ${it.addressTwo}"
                    binding.tvAddress.text = address
                } else {
                    binding.tvAddress.text = "-"
                }
                binding.tvCity.text   = it.city ?: "-"
                binding.tvState.text    = it.state ?: "-"
                binding.tvCountry.text  = it.country ?: "-"
            }
    }

    private fun setDefaultValue() {
        binding.tvProofOfId.text  = "-"
        binding.tvIdNumber.text   = "-"
        binding.tvIdExpiry.text   = "-"
    }

    private fun setOnClickListeners() {
        binding.cvEditProfile.setOnClickListener {
            if (mProfileResponse != null) {
                val bundlePlayerPersonalDetails = Bundle()
                bundlePlayerPersonalDetails.putParcelable("profilePlayerResponse", mProfileResponse)
                Log.d("TAg", "bundlePlayerPersonalDetails: $bundlePlayerPersonalDetails")
                master.openFragment(UpdateProfileFragment(), "UpdateProfileFragment", true, args = bundlePlayerPersonalDetails, popPreviousFragment = false)
            } else {
                master showToast getString(R.string.some_internal_error)
            }
        }

        binding.cvEditEmail.setOnClickListener {
            master.openFragment(PlayerEmailUpdateFragment(), "PlayerEmailUpdateFragment", true, popPreviousFragment = false, isFadeAnimation = true)
        }

        binding.cvEditAddress.setOnClickListener {
            master.openFragment(UpdateAddressFragment(), "UpdateAddressFragment", true, popPreviousFragment = false)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedSlashDate(transactionDate: String) : String {
        return try {
            val parser      = SimpleDateFormat("dd/MM/yyyy")
            val formatter   = SimpleDateFormat("MMM dd, yyyy")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: "-"
        } catch (e: Exception) {
           "-"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDashDate(transactionDate: String) : String {
        return try {
            val parser      = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd, yyyy")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: "-"
        } catch (e: Exception) {
            "-"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(transactionDate: String) : String {
        return try {
            val parser      = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd, yyyy hh:mm aa")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: "-"
        } catch (e: Exception) {
            "-"
        }
    }

    override fun hideKeyboard() {

    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.profile), View.VISIBLE, View.GONE)
    }
}