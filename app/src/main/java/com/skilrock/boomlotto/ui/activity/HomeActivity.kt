package com.skilrock.boomlotto.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.ActivityHomeBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.LogoutSheet
import com.skilrock.boomlotto.models.response.HeaderInfoResponse
import com.skilrock.boomlotto.models.response.LogOutResponse
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse
import com.skilrock.boomlotto.ui.fragment.*
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.drawer.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


class HomeActivity : BaseActivity(), BaseActivity.HeaderInfoListener {

    lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private var mPaymentOptions: PaymentOptionsResponse? = null

    var mFirstTimeWithdrawAmount    = 0.0
    var isInstantGamesViewAllOpen   = false
    private val mSourceWithdraw     = "WITHDRAW"
    private val mSourceMyAccount    = "ACCOUNT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowStyling()
        setLanguage()
        setDataBindingAndViewModel()
        setLanguageToggle()
        setVersion()
        openFragment(HomeFragment(), "HomeFragment", false)
        setOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
        setToolbarElements()
        callHeaderInfoApi()
        checkForRedirection()
    }

    override fun setDataBindingAndViewModel() {
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel   = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.lifecycleOwner  = this
        binding.viewModel       = viewModel
        headerInfoListener      = this

        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataHeaderInfo.observe(this, observerHeaderInfo)
        viewModel.liveDataPaymentOptions.observe(this, observerPaymentOptions)
        viewModel.liveDataHeaderInfoCallback.observe(this, observerHeaderInfoCallback)
        viewModel.liveDataLogout.observe(this,observeLogout)
        viewModel.liveDataNetworkError.observe(this, observerNetworkError)
    }

    private fun setLanguageToggle() {
        if (SharedPrefUtils.getAppLanguage(this) == LANGUAGE_ENGLISH) {
            binding.drawerLayout.tvEn.background = ContextCompat.getDrawable(this, R.drawable.language_selected)
            binding.drawerLayout.tvAr.background = ContextCompat.getDrawable(this, R.drawable.language_unselected)
        } else {
            binding.drawerLayout.tvAr.background = ContextCompat.getDrawable(this, R.drawable.language_selected)
            binding.drawerLayout.tvEn.background = ContextCompat.getDrawable(this, R.drawable.language_unselected)
        }
    }

    private fun setVersion() {
        binding.drawerLayout.tvDrawerVersion.text = (getString(R.string.version) + " " + BuildConfig.VERSION_NAME)
    }

    private fun checkForRedirection() {
        val code = SharedPrefUtils.getHomePageRedirection(this)
        if (code.isNotBlank()) {
            SharedPrefUtils.setHomePageRedirection(this, "")
            if (code == REDIRECTION_CODE_TICKETS)
                openFragment(MyTicketsFragment(), "MyTicketsFragment", true)
            else if (code == REDIRECTION_CODE_INSTANT_GAMES)
                instantGamesViewAllOperation()
        }
    }

    private fun instantGamesViewAllOperation() {
        for (i in 0 until supportFragmentManager.backStackEntryCount)
            supportFragmentManager.popBackStack()

        if (!isInstantGamesViewAllOpen) {
            val homeFragment = supportFragmentManager.findFragmentByTag("HomeFragment") as HomeFragment?
            if (homeFragment != null && homeFragment.isVisible) {
                try {
                    lifecycleScope.launch {
                        delay(200)
                        homeFragment.binding.cardViewAllInstantGames.performClick()
                    }
                } catch (e: Exception) {
                    Log.e("log", "Error: ${e.message}")
                }
            }
        }
    }

    override fun hideKeyboard() {
        //Code here to hide keyboard
    }

    private fun setOnClickListeners() {
        binding.drawerLayout.drawerDrawGames.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, BoomLottoActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.drawerLayout.drawerInstantGames.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            instantGamesViewAllOperation()
        }

        binding.llDrawerIcon.setOnClickListener {
            binding.llDrawerIcon.hideKeyboard()
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.drawerLayout.ivDrawerBoomId.setOnClickListener {
            binding.drawerLayout.drawerMyAccount.performClick()
        }

        binding.drawerLayout.tvPlayerName.setOnClickListener {
            binding.drawerLayout.drawerMyAccount.performClick()
        }

        binding.drawerLayout.tvPlayerName.setOnClickListener {
            binding.drawerLayout.drawerMyAccount.performClick()
        }

        binding.drawerLayout.drawerMyAccount.setOnClickListener {
            if (checkIfLoggedIn()) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                callHeaderInfoApi(mSourceMyAccount)
            }
        }

        binding.drawerLayout.drawerWinnerList.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            openFragment(WinnerFragment(), "WinnerFragment", true)
        }

        binding.drawerLayout.llDrawerHowToPlay.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            openFragment(HowToPlayFragment(), "HowToPlayFragment", true)
        }

        binding.drawerLayout.llDrawerCharity.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            openFragment(CharityFragment(), "CharityFragment", true)
        }

        binding.drawerLayout.tvDrawerTermsAndConditions.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            openFragment(TermsAndConditionsFragment(), "TermsAndConditionsFragment", true)
        }

        binding.drawerLayout.tvDrawerFaq.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            openFragment(FaqFragment(), "FaqFragment", true)
        }

        binding.drawerLayout.tvDrawerSupport.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            openFragment(SupportFragment(), "SupportFragment", true)
        }

        binding.llNotification.setOnClickListener {
            startActivity(Intent(this, PlayerInboxActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.drawerLayout.flNotificationDrawer.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, PlayerInboxActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.drawerLayout.tvDrawerLogout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            val bottomSheetFragment = LogoutSheet(::onLogoutCallback)
            bottomSheetFragment.show((this as AppCompatActivity).supportFragmentManager, LogoutSheet.TAG)
        }

        binding.drawerLayout.llDrawerReferFriend.setOnClickListener {
            if (checkIfLoggedIn()) {
                if (PlayerInfo.getReferFriendCode().isNotEmpty()) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    openFragment(ReferFriendFragment(), "ReferFriendFragment", true, isFadeAnimation = true)
                } else {
                    this showToast getString(R.string.something_went_wrong)
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.drawerLayout.drawerMyResults.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            openFragment(ResultFragment(), "ResultFragment", true)
        }

        binding.drawerLayout.tvEn.setOnClickListener {
            binding.drawerLayout.tvEn.background = ContextCompat.getDrawable(this, R.drawable.language_selected)
            binding.drawerLayout.tvAr.background = ContextCompat.getDrawable(this, R.drawable.language_unselected)
            SharedPrefUtils.setAppLanguage(this, LANGUAGE_ENGLISH)
            LocaleHelper.setLanguage(this, LANGUAGE_ENGLISH)
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }

        binding.drawerLayout.tvAr.setOnClickListener {
            binding.drawerLayout.tvAr.background = ContextCompat.getDrawable(this, R.drawable.language_selected)
            binding.drawerLayout.tvEn.background = ContextCompat.getDrawable(this, R.drawable.language_unselected)
            SharedPrefUtils.setAppLanguage(this, LANGUAGE_ARABIC)
            LocaleHelper.setLanguage(this, LANGUAGE_ARABIC)
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }

        binding.llAddBalance.setOnClickListener {
            if (PlayerInfo.isIdVerified()) {
                startActivity(Intent(this, DepositActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                startActivity(Intent(this, IdVerificationActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        binding.drawerLayout.drawerWithdrawal.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (checkIfLoggedIn()) {
                callHeaderInfoApi(mSourceWithdraw)
            }
        }
    }

    private fun checkIfLoggedIn() : Boolean {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            return false
        }
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (isInstantGamesViewAllOpen) {
                isInstantGamesViewAllOpen = false
                val homeFragment: HomeFragment? = supportFragmentManager.findFragmentByTag("HomeFragment") as HomeFragment?
                if (homeFragment != null && homeFragment.isVisible)
                    homeFragment.setInstantListSpanToTwo()
                else
                    super.onBackPressed()
            } else {
                super.onBackPressed()
            }
        }
    }

    fun openFragment(fragment: Fragment, fragmentTag: String, addToBackStack: Boolean, args: Bundle? = null, isFadeAnimation: Boolean = false, popPreviousFragment: Boolean = true) {
        if (popPreviousFragment)
            supportFragmentManager.popBackStack()

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        if (isFadeAnimation)
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
        else
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)

        if (args != null)
            fragment.arguments = args

        transaction.add(R.id.fragment_container, fragment, fragmentTag)

        if (addToBackStack)
            transaction.addToBackStack(fragmentTag)

        transaction.commit()
        Log.v("log", "Backstack: ${supportFragmentManager.backStackEntryCount}")
    }

    fun setToolbarElements() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            binding.llToolbarNotification.visibility                = View.GONE
            binding.btnLogin.visibility                             = View.VISIBLE
            binding.drawerLayout.tvDrawerLogout.visibility          = View.GONE
            binding.drawerLayout.flNotificationDrawer.visibility    = View.GONE
            binding.drawerLayout.tvPlayerName.text                  = getString(R.string.guest)
        } else {
            binding.drawerLayout.tvDrawerLogout.visibility          = View.VISIBLE
            binding.llToolbarNotification.visibility                = View.VISIBLE
            binding.drawerLayout.flNotificationDrawer.visibility    = View.VISIBLE
            binding.btnLogin.visibility                             = View.GONE
            binding.tvBalance.text                                  = PlayerInfo.getPlayerTotalBalance()
            binding.drawerLayout.tvPlayerName.text                  = if (PlayerInfo.getPlayerFirstName().isBlank()) PlayerInfo.getPlayerUserName() else PlayerInfo.getPlayerFirstName()

            if (PlayerInfo.getBadgeCount(this) > 0) {
                if (PlayerInfo.getBadgeCount(this) < 10) {
                    binding.tvBadgeCount.text                       = PlayerInfo.getBadgeCount(this).toString()
                    binding.drawerLayout.tvBadgeCountDrawer.text    = PlayerInfo.getBadgeCount(this).toString()

                } else {
                    binding.tvBadgeCount.text                       = "9+"
                    binding.drawerLayout.tvBadgeCountDrawer.text    = "9+"
                }
                binding.tvBadgeCount.visibility                     = View.VISIBLE
                binding.drawerLayout.tvBadgeCountDrawer.visibility  = View.VISIBLE

            } else {
                binding.tvBadgeCount.visibility                     = View.GONE
                binding.drawerLayout.tvBadgeCountDrawer.visibility  = View.GONE
            }
        }
    }

    fun setToolbarName(name: String, toolBarTitleVisibility: Int, toolBarIconVisibility: Int) {
        binding.tvToolbarTitle.visibility   = toolBarTitleVisibility
        binding.ivToolBarIcon.visibility    = toolBarIconVisibility
        binding.tvToolbarTitle.text         = name
    }

    private fun callHeaderInfoApi(apiCalledSourceTag: String = "HOME") {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isNotEmpty()) {
            viewModel.callHeaderInfoApi(apiCalledSourceTag)
        }
    }

    private val observerHeaderInfoCallback = Observer<ResponseStatus<HeaderInfoResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val response: HeaderInfoResponse = it.response
                response.unreadMsgCount?.let { unreadCount -> PlayerInfo.setBadgeCount(this, unreadCount) }
                PlayerInfo.setBalance(this, response)
                setToolbarElements()
                if (response.apiCalledSourceTag == mSourceWithdraw)
                    viewModel.callPaymentOptionsApi()
                else if (response.apiCalledSourceTag == mSourceMyAccount) {
                    val bundleProfileStatus = Bundle()
                    it.response.profileStatus.let { profileStatus -> bundleProfileStatus.putString("profileStatus", profileStatus) }
                    openFragment(MyAccountFragment(), "MyAccountFragment", true, args = bundleProfileStatus, isFadeAnimation = true)
                }
            }

            is ResponseStatus.Error -> {
                val errorMessage = getResponseMessage(this, WEAVER, it.errorCode)
                if (WEAVER_SESSION_EXPIRY_CODE != it.errorCode) {
                    ErrorSheet(getString(R.string.boom_five_error), errorMessage, getString(R.string.close)) { }.apply {
                        show(supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.boom_five_error), getString(it.errorMessageCode), getString(R.string.close)) {  }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerPaymentOptions = Observer<ResponseStatus<JSONObject>> { responseStatus ->
        when (responseStatus) {
            is ResponseStatus.Success -> {
                mPaymentOptions = PaymentOptionsResponseParser().handleResponse(responseStatus.response)
                Log.v("log", "Payment Options Model: $mPaymentOptions")
                handlePaymentOptionsResponseForWithdraw()
            }

            is ResponseStatus.Error -> {
                val errorMessage = getResponseMessage(this, CASHIER, responseStatus.errorCode)
                ErrorSheet(getString(R.string.withdrawal_error), errorMessage, getString(R.string.close)) { onBackPressed() }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.withdrawal_error), getString(responseStatus.errorMessageCode), getString(R.string.close)) { onBackPressed() }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun handlePaymentOptionsResponseForWithdraw() {
        mPaymentOptions?.listPayTypeMap?.let { list: ArrayList<PaymentOptionsResponse.PayTypeMap> ->
            if (list.isNotEmpty()) {
                if (PlayerInfo.getRawWithdrawalAbleBalance() < list[0].minValue) {
                    openFragment(WithdrawLowBalanceFragment(), "WithdrawLowBalanceFragment", true, isFadeAnimation = true)
                } else {
                    if (!PlayerInfo.isBankVerified()) {
                        openFragment(WithdrawEmailVerificationFragment(), "WithdrawEmailVerificationFragment", true, isFadeAnimation = true)
                    } else {
                        openFragment(WithdrawalViewPagerFragment(), "WithdrawalViewPagerFragment", true, isFadeAnimation = true)
                    }
                }
                //openFragment(WithdrawalViewPagerFragment(), "WithdrawalViewPagerFragment", true, isFadeAnimation = true)
            } else {
                ErrorSheet(getString(R.string.withdrawal_error), getString(R.string.some_internal_error), getString(R.string.close)) {  }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        } ?: run {
            ErrorSheet(getString(R.string.withdrawal_error), getString(R.string.some_technical_issue), getString(R.string.close)) {  }.apply {
                show(supportFragmentManager, ErrorSheet.TAG)
            }
        }
    }

    private val observeLogout = Observer<ResponseStatus<LogOutResponse>> {
        performLogoutCleanUp(this, Intent(this, HomeActivity::class.java))
    }

    private fun onLogoutCallback() {
        viewModel.callLogoutAPi()
    }

    override fun onHeaderInfoApiResponseCallback() {
        setToolbarElements()
    }

    fun updateDrawerPlayerName(playerName: String) {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isNotEmpty()) {
            binding.drawerLayout.tvPlayerName.text = playerName
        }
    }
}