package com.skilrock.boomlotto.ui.activity

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R.*
import com.skilrock.boomlotto.adapters.BoomPanelAdapter
import com.skilrock.boomlotto.databinding.ActivityBoomLottoBinding
import com.skilrock.boomlotto.dialogs.BoomTicketBuySheet
import com.skilrock.boomlotto.dialogs.DrawSelectionSheet
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.SessionExpirySheet
import com.skilrock.boomlotto.models.BoomPanelData
import com.skilrock.boomlotto.models.request.BoomLottoBuyTicketRequest
import com.skilrock.boomlotto.models.response.BoomLottoBuyTicketResponse
import com.skilrock.boomlotto.models.response.BoomLottoGameResponse
import com.skilrock.boomlotto.models.response.BoomLottoGameResponse.ResponseData.GameRespVO.NumberConfig.Range.Ball
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.BoomLottoViewModel
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BoomLottoActivity : BaseActivity(), BaseActivity.HeaderInfoListener {

    private lateinit var binding: ActivityBoomLottoBinding
    private lateinit var viewModel: BoomLottoViewModel
    private var mSelectedDrawCount = 1
    private val mPanelList = ArrayList<BoomPanelData>()
    private lateinit var mPanelAdapter: BoomPanelAdapter
    private lateinit var mNumberList: ArrayList<Ball?>
    private var isAddLineClickAllowed = false
    private var mPlaySameNumbersAgainList: ArrayList<List<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.enterTransition = null
        setWindowStyling()
        setDataBindingAndViewModel()
        receivePlaySameNumbersAgainData()
        callBoomLottoGameApi()
        setSelectedDrawCount()
        setOnClickListeners()
        disableAddLineClick()
    }

    override fun onResume() {
        super.onResume()
        setToolbarElements()
        callHeaderInfoApi()
    }

    override fun setDataBindingAndViewModel() {
        binding     = DataBindingUtil.setContentView(this, layout.activity_boom_lotto)
        viewModel   = ViewModelProvider(this)[BoomLottoViewModel::class.java]

        binding.lifecycleOwner  = this
        binding.viewModel       = viewModel
        headerInfoListener      = this

        binding.llContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.toolbar.tbNavigationIcon.setImageResource(drawable.icon_back)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataHeaderInfo.observe(this, observerHeaderInfo)
        viewModel.liveDataGameNumbersList.observe(this, observerGameNumbersList)
        viewModel.liveDataBoomGame.observe(this, observerBoomGame)
        viewModel.liveDataHurryUp.observe(this, observerHurryUp)
        viewModel.liveDataBuyTicket.observe(this, observerBuyTicket)
        viewModel.liveDataNetworkError.observe(this, observerNetworkError)
    }

    private fun receivePlaySameNumbersAgainData() {
        val receivedList = intent.getStringArrayListExtra("playedNumbersList")
        if (receivedList != null && receivedList.isNotEmpty() && receivedList.size % 5 == 0) {
            mPlaySameNumbersAgainList = receivedList.windowed(5, step = 5, partialWindows = false) as ArrayList
            Log.v("log", "Play Again Same Numbers Received List: $mPlaySameNumbersAgainList")
        }
    }

    private fun disableAddLineClick() {
        isAddLineClickAllowed = false
    }

    private fun callBoomLottoGameApi() {
        viewModel.callBoomLottoGameApi(false)
    }

    private fun setSelectedDrawCount(count: Int = 1) {
        mSelectedDrawCount          = count
        binding.tvDrawCount.text    = mSelectedDrawCount.toString()
        onBallClicked()
    }

    private fun setBuyAmount(lineCount: Int) {
        binding.tvLineIntoDraw.text         = (lineCount.toString() + " " + getString(string.lines) + " x " + mSelectedDrawCount.toString() + " " + getString(
            string.draws_small))
        binding.tvDonationBadgeCount.text   = (lineCount * mSelectedDrawCount).toString()
        animateLineComplete(binding.tvDonationCountBuyNow, (" x " + (lineCount * mSelectedDrawCount)))

        val amount = viewModel.mUnitPrice * mSelectedDrawCount * lineCount
        binding.tvBuyNowAmount.text = (BuildConfig.CURRENCY_CODE + " " + getFormattedAmount(amount))
    }

    private fun animateLineComplete(textView: MaterialTextView, text: String) {
        if (textView.text != text) {
            textView.text = text
            textView.animate()
                .scaleX(2f).scaleY(2f)
                .setDuration(200)
                .withEndAction {
                    textView.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(200)
                        .withEndAction { }
                }
        }
    }

    private val observerGameNumbersList = Observer<ArrayList<Ball?>> { numberList ->
        mNumberList     = numberList
        mPanelAdapter   = BoomPanelAdapter(this, viewModel, this, mPanelList, viewModel.mMaximumSelectionLimit,
            viewModel.mMaxPanelAllowed, ::onBallClicked) { binding.tvAddLine.visibility = View.VISIBLE }

        binding.rvPanel.apply {
            layoutManager = LinearLayoutManager(this@BoomLottoActivity)
            adapter = mPanelAdapter
        }

        val playSameNumbersAgainList = mPlaySameNumbersAgainList
        if (playSameNumbersAgainList != null && playSameNumbersAgainList.isNotEmpty())
            addPreviouslyPlayedNumbersPanel()
        else
            addNewPanel()
    }

    private fun onBallClicked() {
        isAddLineClickAllowed = true

        var numberOfLines = 0
        mPanelList.forEach { panelData ->
            var count = 0
            panelData.listNumbers.forEach { ballInfo -> if (ballInfo.isClicked) count++ }
            if (count != viewModel.mMaximumSelectionLimit) { isAddLineClickAllowed = false }
            if (count == viewModel.mMaximumSelectionLimit) numberOfLines++
        }
        setBuyAmount(numberOfLines)
    }

    private val observerBoomGame = Observer<ResponseStatus<BoomLottoGameResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                Log.i("log", "Boom 5 Game Response Received Successfully.")
            }

            is ResponseStatus.Error -> {
                val errorMessage = getResponseMessage(this, DMS, it.errorCode)
                ErrorSheet(getString(string.boom_five_error), errorMessage, getString(
                    string.close)) { onBackPressed() }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(string.boom_five_error), getString(it.errorMessageCode), getString(
                    string.close)) { onBackPressed() }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.toolbar.llDrawerIcon.setOnClickListener { onBackPressed() }

        binding.toolbar.llAddBalance.setOnClickListener {
            if (PlayerInfo.isIdVerified()) {
                startActivity(Intent(this, DepositActivity::class.java))
                overridePendingTransition(anim.slide_in_right, anim.slide_out_left)
            } else {
                startActivity(Intent(this, IdVerificationActivity::class.java))
                overridePendingTransition(anim.slide_in_right, anim.slide_out_left)
            }
        }

        binding.toolbar.llNotification.setOnClickListener {
            startActivity(Intent(this, PlayerInboxActivity::class.java))
            overridePendingTransition(anim.slide_in_right, anim.slide_out_left)
        }

        binding.toolbar.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(anim.slide_in_right, anim.slide_out_left)
        }

        binding.tvAddLine.setOnClickListener {
            if (isAddLineClickAllowed) {
                addNewPanel()
                isAddLineClickAllowed           = false
                binding.tvAddLine.visibility    = if (mPanelList.size == viewModel.mMaxPanelAllowed) View.INVISIBLE else View.VISIBLE

                lifecycleScope.launch {
                    delay(600)
                    binding.scrollView.smoothScrollTo(0, binding.rvPanel.height)
                }
            } else {
                showToast(getString(string.please_complete_previous_incomplete_lines))
                binding.tvAddLine.startAnimation(shakeError())
            }
        }

        binding.llSelectDraws.setOnClickListener {
            DrawSelectionSheet(viewModel.mDrawList.size) { count ->
                mSelectedDrawCount          = count
                binding.tvDrawCount.text    = mSelectedDrawCount.toString()
                setBuyAmount(getNumberOfLines())
            }.apply {
                show(supportFragmentManager, DrawSelectionSheet.TAG)
            }
        }

        binding.llBuyNow.setOnClickListener { validateTicketBuy() }
    }

    private fun addPreviouslyPlayedNumbersPanel() {
        val playSameNumbersAgainList = mPlaySameNumbersAgainList
        if (playSameNumbersAgainList != null && playSameNumbersAgainList.isNotEmpty()) {
            playSameNumbersAgainList.forEach { previouslySelectedList: List<String> ->
                val ballList = ArrayList<BoomPanelData.BallInfo>()
                mNumberList.forEach { ball -> ball?.number?.let {
                    ballList.add(BoomPanelData.BallInfo(number = it, isClicked = previouslySelectedList.contains(it))) }
                }

                val boomPanelData = BoomPanelData(
                    MutableLiveData(getString(string.line) + " " + (mPanelList.size + 1)), ballList, isQuickPick = false, isCardOpen = true,
                    isQuickPickAllowed = viewModel.mIsQuickPickAllowed.value ?: false, reachedMaxSelectionCount = true
                )
                mPanelList.add(boomPanelData)

                mPanelList.forEachIndexed { index, panelData ->
                    panelData.isCardOpen = index == (mPanelList.size - 1)
                    mPanelAdapter.notifyItemChanged(index)
                }
            }
            isAddLineClickAllowed           = true
            binding.tvAddLine.visibility    = if (mPanelList.size == viewModel.mMaxPanelAllowed) View.INVISIBLE else View.VISIBLE
            setBuyAmount(getNumberOfLines())
        } else
            addNewPanel()
    }

    private fun addNewPanel() {
        val ballList = ArrayList<BoomPanelData.BallInfo>()
        mNumberList.forEach { ball -> ball?.number?.let { ballList.add(BoomPanelData.BallInfo(number = it)) } }

        val boomPanelData = BoomPanelData(MutableLiveData(getString(string.line) + " " + (mPanelList.size + 1)), ballList,
            isQuickPick = false,
            isCardOpen = true,
            isQuickPickAllowed = viewModel.mIsQuickPickAllowed.value ?: false
        )
        mPanelList.add(boomPanelData)

        mPanelList.forEachIndexed { index, panelData ->
            panelData.isCardOpen = index == (mPanelList.size - 1)
            mPanelAdapter.notifyItemChanged(index)
        }
    }

    private fun getNumberOfLines() : Int {
        var numberOfLines = 0
        mPanelList.forEach { panelData ->
            var count = 0
            panelData.listNumbers.forEach { ballInfo -> if (ballInfo.isClicked) count++ }
            if (count == viewModel.mMaximumSelectionLimit) numberOfLines++
        }

        return  numberOfLines
    }

    override fun hideKeyboard() {
        //binding.btnProceed.hideKeyboard()
    }

    fun setToolbarElements() {
        binding.toolbar.tvToolbarLabel.text                     = getString(string.boom_five)
        binding.toolbar.tvToolbarLabel.visibility               = View.VISIBLE
        binding.toolbar.ivToolBarIcon.visibility                = View.GONE

        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            binding.toolbar.llToolbarNotification.visibility    = View.GONE
            binding.toolbar.btnLogin.visibility                 = View.VISIBLE
        } else {
            binding.toolbar.llToolbarNotification.visibility    = View.VISIBLE
            binding.toolbar.btnLogin.visibility                 = View.GONE
            binding.toolbar.tvBalance.text                      = HtmlCompat.fromHtml(PlayerInfo.getPlayerTotalBalanceBold(), HtmlCompat.FROM_HTML_MODE_LEGACY)

            if (PlayerInfo.getBadgeCount(this) > 0) {
                if (PlayerInfo.getBadgeCount(this) < 10) {
                    binding.toolbar.tvBadgeCount.text           = PlayerInfo.getBadgeCount(this).toString()

                } else {
                    binding.toolbar.tvBadgeCount.text           = "9+"
                }
                binding.toolbar.tvBadgeCount.visibility         = View.VISIBLE

            } else {
                binding.toolbar.tvBadgeCount.visibility         = View.GONE
            }
        }
    }

    private val observerHurryUp = Observer<Boolean> {
        if (it) {
            binding.tvAlert.visibility  = View.VISIBLE
            binding.tvAlert.text        = getString(string.hurry_up)
            binding.tvAlert.startAnimation(AnimationUtils.loadAnimation(this, anim.hurry_up_animation))
        } else {
            binding.tvAlert.visibility  = View.VISIBLE
            binding.tvAlert.text        = getString(string.you_are_playing_for_next_day)
            binding.tvAlert.clearAnimation()
        }
    }

    private fun callHeaderInfoApi() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isNotEmpty()) {
            viewModel.callHeaderInfoApi()
        }
    }

    private fun validateTicketBuy() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(anim.slide_in_right, anim.slide_out_left)
        } else {
            val isAllLineFilled = mPanelList.all { boomPanelData -> boomPanelData.listNumbers.count { it.isClicked } == viewModel.mMaximumSelectionLimit }
            if (isAllLineFilled) {
                buyTicket()
            } else {
                showToast(getString(string.please_complete_delete_incomplete_lines))
                binding.llBuyNow.startAnimation(shakeError())
            }
        }
    }

    private fun buyTicket() {
        val panelList   = ArrayList<BoomLottoBuyTicketRequest.PanelData>()
        val betType     = viewModel.mBoomResponse.betRespVOs?.get(0)?.betCode
        val pickType    = viewModel.mBoomResponse.betRespVOs?.get(0)?.pickTypeData?.pickType?.get(0)?.code
        val pickConfig  = viewModel.mBoomResponse.betRespVOs?.get(0)?.pickTypeData?.pickType?.get(0)?.range?.get(0)?.pickConfig
        val drawCount   = mSelectedDrawCount
        val gameCode    = viewModel.mBoomResponse.gameCode
        val gameId      = viewModel.mBoomResponse.id.toString()

        mPanelList.forEach { panelData ->
            val selectedNumbers = panelData.listNumbers.filter { it.isClicked }
            val isQuickPick     = panelData.isQuickPick
            val totalNumbers    = selectedNumbers.size
            val pickedValues    = selectedNumbers.joinToString(prefix = "", postfix = "", separator = ",") { it.number }

            val panel = BoomLottoBuyTicketRequest.PanelData(betType = betType, pickType = pickType,
                pickConfig = pickConfig, quickPick = isQuickPick, totalNumbers = totalNumbers,
                pickedValues = pickedValues, qpPreGenerated = isQuickPick)
            panelList.add(panel)
        }

        val ticketRequest = BoomLottoBuyTicketRequest(noOfDraws = drawCount, gameCode = gameCode,
            gameId = gameId, panelData = panelList, merchantData = BoomLottoBuyTicketRequest.MerchantData())

        viewModel.callBuyTicketApi(ticketRequest)
    }

    private val observerBuyTicket = Observer<ResponseStatus<BoomLottoBuyTicketResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val ticketData = it.response.responseData
                if (ticketData != null) {
                    openTicketInfoSheet(ticketData)
                } else {
                    ErrorSheet(getString(string.boom_five_error), getString(string.some_internal_error), getString(string.close)) {  }.apply {
                        show(supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }

            is ResponseStatus.Error -> {
                if (WEAVER_SESSION_EXPIRY_CODE == it.errorCode) {
                    SessionExpirySheet( { performLogoutCleanUp(this, Intent(this, HomeActivity::class.java)) } ) {
                        startActivity(Intent(this, LoginActivity::class.java))
                        overridePendingTransition(anim.slide_in_right, anim.slide_out_left)
                    }.apply {
                        show(supportFragmentManager, SessionExpirySheet.TAG)
                    }
                } else {
                    val errorMessage = getResponseMessage(this, DMS, it.errorCode)
                    ErrorSheet(getString(string.boom_five_error), errorMessage, getString(string.close)) { }.apply {
                        show(supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(string.boom_five_error), getString(it.errorMessageCode), getString(string.close)) {  }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun openTicketInfoSheet(ticketData: BoomLottoBuyTicketResponse.ResponseData) {
        val donation        = ticketData.productInfo?.donation
        val lines           = ticketData.panelData?.size ?: 0
        val draws           = ticketData.drawData?.size ?: 0
        val title1          = (lines * draws).toString() + " " + (if (donation != null && donation.isNotEmpty()) donation[0]?.title ?: "----" else "----")
        val title2          = getString(string.and_played) + " " + lines + " " + (if (lines > 1) getString(string.lines_of) else getString(string.line_of)) + " " + draws + " " + (if (draws > 1) getString(string.draws_of) else getString(string.draw_of))
        val amount          = getString(string.total) + " ${PlayerInfo.getCurrency()} " + if (ticketData.totalPurchaseAmount != null) getFormattedAmount(ticketData.totalPurchaseAmount.toString().toDouble()) else "----"
        val ticketNumber    = ticketData.ticketNumber ?: "----"
        val url             = if (donation != null && donation.isNotEmpty()) donation[0]?.image ?: "" else ""
        BoomTicketBuySheet(title1, title2, amount, ticketNumber, url, ::onCloseClick, ::onPlayAgainClick, ::onPlayInstantGamesClick, ::onTicketListClick).apply {
            show(supportFragmentManager, BoomTicketBuySheet.TAG)
        }
    }

    private fun onPlayAgainClick() {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        startActivity(Intent(this, BoomLottoActivity::class.java), options.toBundle())
        lifecycleScope.launch {
            delay(400)
            finish()
        }
    }

    private fun onPlayInstantGamesClick() {
        SharedPrefUtils.setHomePageRedirection(this, REDIRECTION_CODE_INSTANT_GAMES)
        onBackPressed()
    }

    private fun onTicketListClick() {
        SharedPrefUtils.setHomePageRedirection(this, REDIRECTION_CODE_TICKETS)
        onBackPressed()
    }

    private fun onCloseClick() {
        onBackPressed()
    }

    override fun onDestroy() {
        window.exitTransition = null
        super.onDestroy()
    }

    override fun onHeaderInfoApiResponseCallback() {
        setToolbarElements()
    }
}