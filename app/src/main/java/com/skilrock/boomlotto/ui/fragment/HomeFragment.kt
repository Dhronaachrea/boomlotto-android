package com.skilrock.boomlotto.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.*
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.HomeBannerAdapter
import com.skilrock.boomlotto.adapters.HomeResultListAdapter
import com.skilrock.boomlotto.adapters.HomeWinnerListAdapter
import com.skilrock.boomlotto.adapters.InstantGamesAdapter
import com.skilrock.boomlotto.databinding.FragmentHomeBinding
import com.skilrock.boomlotto.models.response.BoomGameInfoResponse
import com.skilrock.boomlotto.models.response.InstantGameListResponse.Data.Ige.Engines.DUBAI.Game
import com.skilrock.boomlotto.models.response.ResultResponse
import com.skilrock.boomlotto.models.response.WinnerListResponse
import com.skilrock.boomlotto.ui.activity.BoomLottoActivity
import com.skilrock.boomlotto.ui.activity.InstantGamePlayActivity
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment() {

    lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var bannerAdapter: HomeBannerAdapter
    private var mBannerListSize = 0
    private var isBannerSliderOn = true
    private var mInstantGamesBaseUrl = ""
    private var mSpanCount = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bannerAdapter = HomeBannerAdapter(master)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setBannerRecyclerView()
        setInstantGamesRecyclerView()
        callBoomInfoApi()
        callResultApi()
        callWinnerListApi()
        setOnClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoaderBoomInfo.observe(master, observerLoaderBoomInfo)
        viewModel.liveDataHideKeyboard.observe(master, observerHideKeyboard)
        viewModel.liveDataBoomGameInfo.observe(master, observerBoomGameInfo)
        viewModel.liveDateWinnerList.observe(master, observerDrawWinnerList)
        viewModel.liveDataResultList.observe(master, observerResultList)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
        viewModel.liveDataBoomUnitPrice.observe(master, observerBoomUnitPrice)
    }

    private fun callBoomInfoApi() {
        viewModel.callBoomGameInfo()
    }

    private fun callWinnerListApi() {
        viewModel.callWinnerListApi()
    }

    private fun callResultApi() {
        viewModel.callResultApi(viewModel.getDateForRequestParam(getPreviousDate(30)), viewModel.getDateForRequestParam(getCurrentDate()))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setOnClickListeners() {
        binding.pullToRefresh.setOnRefreshListener {
            viewModel.callBoomGameInfo()
            binding.pullToRefresh.isRefreshing  = false
        }
        binding.pullToRefresh.setColorSchemeResources(R.color.color_app_pink)

        binding.llBoomLotto.setOnClickListener {
            val pairLogo    = androidx.core.util.Pair<View, String>(binding.ivBoomFiveLogo, "boom_logo")
            val options     = ActivityOptionsCompat.makeSceneTransitionAnimation(master, pairLogo)
            startActivity(Intent(master, BoomLottoActivity::class.java), options.toBundle())
        }

        binding.btnBoomPlayNow.setOnClickListener {
            val pairLogo    = androidx.core.util.Pair<View, String>(binding.ivBoomFiveLogo, "boom_logo")
            val options     = ActivityOptionsCompat.makeSceneTransitionAnimation(master, pairLogo)
            startActivity(Intent(master, BoomLottoActivity::class.java), options.toBundle())
        }

        binding.cardViewAllInstantGames.setOnClickListener {
            master.isInstantGamesViewAllOpen        = true
            binding.llInstantViewAllBar.visibility  = View.GONE
            binding.cardBoom.visibility             = View.GONE
            binding.rvHomeBanner.visibility         = View.GONE
            mSpanCount                              = 1

            (binding.rvInstantGames.adapter as InstantGamesAdapter).apply {
                span = mSpanCount
                notifyDataSetChanged()
            }

            binding.rvInstantGames.post {
                TransitionManager.beginDelayedTransition(binding.rvInstantGames)
                (binding.rvInstantGames.layoutManager as GridLayoutManager).spanCount = mSpanCount
            }
        }

        binding.cardViewAllResultList.setOnClickListener {
            master.openFragment(ResultFragment(), "ResultFragment", true)
        }

        binding.cardViewAllWinnerList.setOnClickListener {
            master.openFragment(WinnerFragment(), "WinnerFragment", true)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setInstantListSpanToTwo() {
        master.isInstantGamesViewAllOpen        = false
        binding.llInstantViewAllBar.visibility  = View.VISIBLE
        binding.rvHomeBanner.visibility         = View.VISIBLE
        binding.cardBoom.visibility             = View.VISIBLE
        mSpanCount = 2

        (binding.rvInstantGames.adapter as InstantGamesAdapter).apply {
            span = mSpanCount
            notifyDataSetChanged()
        }

        binding.rvInstantGames.post {
            TransitionManager.beginDelayedTransition(binding.rvInstantGames)
            (binding.rvInstantGames.layoutManager as GridLayoutManager).spanCount = mSpanCount
        }
    }

    private fun setBannerRecyclerView() {
        val bannerList = SharedPrefUtils.getBanner(master)

        if (bannerList == null || bannerList.isEmpty())
            binding.rvHomeBanner.visibility = View.GONE
        else {
            mBannerListSize = bannerList.size
            binding.rvHomeBanner.visibility = View.VISIBLE
            binding.rvHomeBanner.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return true
                }
            })

            binding.rvHomeBanner.apply {
                layoutManager = LinearLayoutManager(master, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = bannerAdapter
            }
            bannerAdapter.setBannerListData(bannerList)
            setBannerSlide()
        }
    }

    private fun setInstantGamesRecyclerView() {
        val savedResponse = SharedPrefUtils.getInstantGameListResponse(master)

        if (savedResponse != null) {
            val games = savedResponse.data?.ige?.engines?.dUBAI?.games
            games?.let { list: ArrayList<Game?> ->
                if (list.isNotEmpty()) {

                    //binding.cardViewAllInstantGames.visibility = if (list.size < 5) View.INVISIBLE else View.VISIBLE

                    list.forEach { game ->
                        val mapImage = game?.loaderImage
                        if (mapImage != null && mapImage.isNotEmpty()) {
                            val entries: List<String> = mapImage.toList().map { it.second }
                            if (entries.isNotEmpty()) {
                                game.imagePathLarge = entries[entries.size - 1]
                            } else
                                game.imagePathLarge = game.imagePath
                        } else {
                            game?.imagePathLarge = game?.imagePath
                        }

                        val mapPrize = game?.prizeSchemes
                        if (mapPrize != null && mapPrize.isNotEmpty()) {
                            val entries: List<String> = mapPrize.toList().map { it.second }
                            if (entries.isNotEmpty()) {
                                game.amount = entries[0]
                            } else
                                game.amount = "-"
                        } else {
                            game?.amount = "-"
                        }
                    }

                    val gameAdapter = InstantGamesAdapter(list, master, 2, ::onClickInstantGames)
                    binding.rvInstantGames.apply {
                        layoutManager = GridLayoutManager(master, 2)
                        setHasFixedSize(true)
                        adapter = gameAdapter
                    }
                } else {
                    binding.rvInstantGames.visibility = View.GONE
                    binding.llInstantViewAllBar.visibility = View.GONE
                }
            }

            savedResponse.data?.ige?.engines?.dUBAI?.params?.repo?.let { url ->
                mInstantGamesBaseUrl = url
            }
        } else {
            binding.rvInstantGames.visibility = View.GONE
            binding.llInstantViewAllBar.visibility = View.GONE
        }
    }

    private fun onClickInstantGames(game: Game) {
        if (mInstantGamesBaseUrl.isBlank() || game.gameNumber == null) {
            master.showToast(getString(R.string.information_not_available))
        } else {
            val params = SharedPrefUtils.getInstantGameListResponse(master)?.data?.ige?.engines?.dUBAI?.params

            if (params != null) {
                val url = viewModel.prepareInstantGameUrl(params, game)
                val instantGameIntent = Intent(master, InstantGamePlayActivity::class.java)
                instantGameIntent.putExtra("gameCode", game.gameNumber.toString())
                instantGameIntent.putExtra("gameUrl", url)
                startActivity(instantGameIntent)
                master.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else
                master.showToast(getString(R.string.something_went_wrong))
        }
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {

    }

    private val observerResultList = Observer<ResponseStatus<ResultResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val boomGame = it.response.responseData?.find { data -> data?.gameCode == BOOM_GAME_CODE }
                val resultList = boomGame?.resultData

                if (resultList != null && resultList.isNotEmpty()) {
                    if (resultList.size == 0) {
                        binding.llHomeResultList.visibility = View.GONE
                    } else {
                        prepareDataForResultsRecyclerView(resultList)
                    }
                }
            }

            is ResponseStatus.Error -> {
                binding.llHomeResultList.visibility = View.GONE
            }

            is ResponseStatus.TechnicalError -> {
                binding.llHomeResultList.visibility = View.GONE
            }
        }
    }

    private fun prepareDataForResultsRecyclerView(resultList: ArrayList<ResultResponse.ResponseData.ResultData?>) {

        val listResults: ArrayList<ResultResponse.ResponseData.ResultData.ResultInfo> = ArrayList()
        resultList.forEach { resultData ->
            resultData?.resultInfo?.forEach { resultInfo ->
                resultInfo?.let {
                    it.resultDate = resultData.resultDate
                    listResults.add(it)
                }
            }
        }

        if (listResults.isNotEmpty()) {
            listResults.forEachIndexed { index, resultInfo -> resultInfo.rowIndex = index }
            binding.rvResultList.apply {
                val homeResultAdapter = HomeResultListAdapter(master, listResults)
                layoutManager = LinearLayoutManager(master, LinearLayoutManager.HORIZONTAL, false)
                adapter = homeResultAdapter
            }
            PagerSnapHelper().attachToRecyclerView(binding.rvResultList)
            binding.llHomeResultList.visibility = View.VISIBLE
        } else {
            binding.llHomeResultList.visibility = View.GONE
        }
    }

    private val observerDrawWinnerList = Observer<ResponseStatus<WinnerListResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val dgeList = it.response.data?.dGE

                if (dgeList == null || dgeList.isEmpty()) {
                    binding.llHomeWinnerList.visibility     = View.GONE
                } else {
                    val adapterDge = HomeWinnerListAdapter(master, dgeList)
                    binding.rvDrawWinnerList.apply {
                        layoutManager   = LinearLayoutManager(master, LinearLayoutManager.HORIZONTAL, false)
                        adapter         = adapterDge
                    }
                    binding.llHomeWinnerList.visibility = View.VISIBLE
                }
            }

            is ResponseStatus.Error -> {
                binding.llHomeWinnerList.visibility     = View.GONE
            }

            is ResponseStatus.TechnicalError -> {
                binding.llHomeWinnerList.visibility     = View.GONE
            }
        }
    }

    private val observerBoomGameInfo = Observer<ResponseStatus<BoomGameInfoResponse>> {
        when(it) {
            is ResponseStatus.Success -> { }

            is ResponseStatus.Error -> {
                val errorMessage = master.getResponseMessage(master, CMS, it.errorCode)
                master.showToast(errorMessage)
            }

            is ResponseStatus.TechnicalError -> master.showToast(getString(it.errorMessageCode))
        }
    }

    private val observerBoomUnitPrice = Observer<String> {
        SharedPrefUtils.setBoomUnitPrice(master, it)
    }

    private fun setBannerSlide() {
        if (isBannerSliderOn) {
            isBannerSliderOn = false
            lifecycleScope.launch {
                var position = 0
                while (true) {
                    if (position == 0)
                        binding.rvHomeBanner.scrollToPosition(position)
                    else
                        binding.rvHomeBanner.smoothSnapToPosition(position)
                    delay(5000)
                    if (position == mBannerListSize - 1)
                        position = 0
                    else
                        position++
                }
            }
        }
    }

    private val observerLoaderBoomInfo = Observer<Boolean> {
        if (it) {
            binding.progressBarBoomInfo.animate().alpha(1f).setDuration(50).withEndAction {
                master.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                binding.progressBarBoomInfo.visibility = View.VISIBLE
            }
        }
        else {
            binding.progressBarBoomInfo.animate().alpha(0f).setDuration(50).withEndAction {
                binding.progressBarBoomInfo.visibility = View.INVISIBLE
                master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }
}