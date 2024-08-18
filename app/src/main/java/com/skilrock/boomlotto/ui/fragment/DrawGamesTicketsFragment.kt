package com.skilrock.boomlotto.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.DgeTicketsAdapter
import com.skilrock.boomlotto.databinding.FragmentDrawGamesTicketsBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.TicketDetailSheet
import com.skilrock.boomlotto.models.request.MyTicketDetailRequest
import com.skilrock.boomlotto.models.response.MyTicketDetailResponse
import com.skilrock.boomlotto.models.response.MyTicketsListResponse
import com.skilrock.boomlotto.models.response.ServerTimeResponse
import com.skilrock.boomlotto.ui.activity.BoomLottoActivity
import com.skilrock.boomlotto.utility.BOOM_GAME_CODE
import com.skilrock.boomlotto.utility.DMS
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.utility.showToast
import com.skilrock.boomlotto.viewmodels.MyTicketsViewModel
import java.text.SimpleDateFormat

class DrawGamesTicketsFragment : BaseFragment() {

    private lateinit var binding: FragmentDrawGamesTicketsBinding
    private lateinit var viewModel: MyTicketsViewModel
    private var mTicketList: ArrayList<MyTicketsListResponse.ResponseData.Ticket?>? = null
    private lateinit var mAdapter: DgeTicketsAdapter
    private var mSelectedDrawId = -1

    private var mTicketData: MyTicketDetailResponse.ResponseData? = null
    private var mDrawData: MyTicketDetailResponse.ResponseData.DrawWin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = DgeTicketsAdapter(master, ::onTicketClick)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw_games_tickets, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setRecyclerView()
        viewModel.callTicketListApi()
        setListener()
    }

    private fun setListener() {
        binding.pullToRefresh.setOnRefreshListener {
            binding.rvDrawTickets.visibility    = View.GONE
            binding.animationView.visibility    = View.VISIBLE
            binding.llNoData.visibility         = View.GONE
            viewModel.callTicketListApi()
            binding.pullToRefresh.isRefreshing  = false
        }

        binding.pullToRefresh.setColorSchemeResources(R.color.color_app_pink)

        binding.ietTicketDrawGamesSearchInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                ticketDrawGamesSearchFilter(p0.toString())
            }
        })
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[MyTicketsViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
        viewModel.liveDataTicketList.observe(master, observerTicketList)
        viewModel.liveDataTicketDetail.observe(master, observerTicketDetail)
        viewModel.liveDataServerTime.observe(master, observerServerTime)
    }

    private fun setRecyclerView() {
        binding.rvDrawTickets.apply {
            layoutManager = LinearLayoutManager(master)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private val observerTicketList = Observer<ResponseStatus<MyTicketsListResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val ticketData: MyTicketsListResponse.ResponseData? = it.response.responseData?.find { data -> data?.gameCode == BOOM_GAME_CODE }
                if (ticketData == null) {
                    binding.rvDrawTickets.visibility    = View.GONE
                    binding.animationView.visibility    = View.GONE
                    binding.llNoData.visibility         = View.VISIBLE
                } else {
                    val ticketList: ArrayList<MyTicketsListResponse.ResponseData.Ticket?>? = ticketData.ticketList
                    if (ticketList == null || ticketList.isEmpty()) {
                        binding.rvDrawTickets.visibility    = View.GONE
                        binding.animationView.visibility    = View.GONE
                        binding.llNoData.visibility         = View.VISIBLE
                    } else {
                        binding.rvDrawTickets.visibility    = View.VISIBLE
                        binding.animationView.visibility    = View.GONE
                        binding.llNoData.visibility         = View.GONE
                        ticketList.forEach { ticket ->
                            ticket?.gameDisplayName = ticketData.gameName ?: getString(R.string.na)
                        }

                        mTicketList = ticketList
                        mAdapter.putTicketList(ticketList)
                        binding.rvDrawTickets.scheduleLayoutAnimation()
                    }
                }
            }

            is ResponseStatus.Error -> {
                binding.rvDrawTickets.visibility    = View.GONE
                binding.animationView.visibility    = View.GONE
                binding.llNoData.visibility         = View.VISIBLE
                val errorMessage = master.getResponseMessage(master, DMS, it.errorCode)
                ErrorSheet(getString(R.string.boom_lotto_error), errorMessage, master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                binding.rvDrawTickets.visibility    = View.GONE
                binding.animationView.visibility    = View.GONE
                binding.llNoData.visibility         = View.VISIBLE
                ErrorSheet(getString(R.string.boom_lotto_error), getString(it.errorMessageCode), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerTicketDetail = Observer<ResponseStatus<MyTicketDetailResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val ticketData = it.response.responseData
                if (ticketData != null) {
                    val drawData = ticketData.drawWinList?.find { draw -> draw?.drawId == mSelectedDrawId }
                    if (drawData != null) {
                        mTicketData = ticketData
                        mDrawData   = drawData
                        if (drawData.winResult == null || drawData.winResult.isBlank() || drawData.winResult == "RESULT AWAITED") {
                            viewModel.callServerTimeApi()
                        } else {
                            //TicketDetailDialog(master, ticketData, drawData).showDialog()
                            TicketDetailSheet(master, ticketData, drawData, mSelectedDrawId = mSelectedDrawId, onPlaySameNumbersAgain = ::onPlaySameNumbersAgain).apply {
                                show(master.supportFragmentManager, TicketDetailSheet.TAG)
                            }
                        }
                    }
                    else
                        master.showToast(getString(R.string.ticket_information_not_available_1))
                } else
                    master.showToast(getString(R.string.ticket_information_not_available_2))
            }

            is ResponseStatus.Error -> {
                mTicketData = null
                mDrawData   = null
                master.showToast(master.getResponseMessage(master, DMS, it.errorCode))
            }

            is ResponseStatus.TechnicalError -> {
                mTicketData = null
                mDrawData   = null
                master.showToast(getString(it.errorMessageCode))
            }
        }
    }

    private val observerServerTime = Observer<ResponseStatus<ServerTimeResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                it.response.data?.date?.let { serverTime ->
                    mDrawData?.drawDateTime?.let { drawTime ->
                        showTicketDialogWithTimer(getString(R.string.ticket_information_not_available_13), viewModel.getTimeDifference(drawTime, serverTime))
                    } ?: run {
                        showTicketDialogWithTimer(getString(R.string.ticket_information_not_available_12))
                    }
                } ?: run {
                    showTicketDialogWithTimer(getString(R.string.ticket_information_not_available_11))
                }
            }

            is ResponseStatus.Error -> showTicketDialogWithTimer(getString(R.string.ticket_information_not_available_9))

            is ResponseStatus.TechnicalError ->
                showTicketDialogWithTimer(getString(R.string.ticket_information_not_available_10))

        }
    }

    private fun showTicketDialogWithTimer(errorMessage: String, timeDiff: Long = 0) {
        val ticketData  = mTicketData
        val drawData    = mDrawData
        if (ticketData != null && drawData != null) {
            TicketDetailSheet(master, ticketData, drawData, timeDiff, mSelectedDrawId = mSelectedDrawId, onPlaySameNumbersAgain = ::onPlaySameNumbersAgain).apply {
                show(master.supportFragmentManager, TicketDetailSheet.TAG)
            }
        } else
            master.showToast(errorMessage)
    }

    private fun onTicketClick(ticket: MyTicketsListResponse.ResponseData.Ticket) {
        ticket.drawDetails?.drawId?.let { drawId ->
            ticket.ticketNumber?.let { ticketNumber ->
                mSelectedDrawId = drawId
                viewModel.callTicketDetailApi(MyTicketDetailRequest(ticketNumber = ticketNumber))
            } ?: run { master.showToast(getString(R.string.ticket_information_not_available_3)) }
        } ?: run { master.showToast(getString(R.string.ticket_information_not_available_4)) }
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {

    }

    private fun onPlaySameNumbersAgain(listNumbers: ArrayList<ArrayList<String>>) {
        val list = listNumbers.flatten() as ArrayList<String>
        val intentBoom = Intent(master, BoomLottoActivity::class.java)
        intentBoom.putStringArrayListExtra("playedNumbersList", list)
        master.startActivity(intentBoom)
    }

    fun ticketDrawGamesSearchFilter(searchedInputText:String) {
        val temporaryList       = ArrayList<MyTicketsListResponse.ResponseData.Ticket?>()
        val transactionResponse = viewModel.liveDataTicketList.value
        transactionResponse?.let {
            when(it) {
                is ResponseStatus.Success -> {
                    it.response.responseData.let { commingResponse ->
                        commingResponse?.forEach { responseData ->
                            if (responseData != null) {
                                responseData.ticketList?.forEach{ ticketList ->
                                    if (ticketList != null) {
                                        when {
                                            getDisplayTransactionDate(ticketList.drawDetails?.drawDateTime).lowercase().contains(searchedInputText.lowercase()) -> {
                                                temporaryList.add(ticketList)
                                            }
                                            "${ticketList.ticketDetails?.txnCurrency} ${ticketList.ticketDetails?.saleAmount.toString()}".lowercase().contains(searchedInputText.lowercase()) || "${ticketList.ticketDetails?.txnCurrency} ${ticketList.ticketDetails?.winningAmount.toString()}".lowercase().contains(searchedInputText.lowercase()) -> {
                                                temporaryList.add(ticketList)
                                            }
                                            ticketList.gameDisplayName.lowercase().contains(searchedInputText.lowercase()) -> {
                                                temporaryList.add(ticketList)
                                            }
                                            "you won".contains(searchedInputText.lowercase()) || "mabrook!".contains(searchedInputText.lowercase()) || "boom5".contains(searchedInputText.lowercase()) -> {
                                                if (((ticketList.ticketDetails?.winstatus?.lowercase()?.contains("win") == true) && (!ticketList.ticketDetails.winstatus.lowercase().contains("non")))) {
                                                    temporaryList.add(ticketList)
                                                }
                                            }
                                            ticketList.ticketDetails?.winstatus?.lowercase()?.contains(searchedInputText.lowercase()) == true  || "result awaited".contains(searchedInputText.lowercase()) -> {
                                                if (searchedInputText.lowercase().contains("win") && !searchedInputText.lowercase().contains("non")) {
                                                    if (ticketList.ticketDetails?.winstatus?.lowercase()?.contains("non") == false) {
                                                        temporaryList.add(ticketList)
                                                    }
                                                } else if ("result awaited".contains(searchedInputText.lowercase())) {
                                                    if (ticketList.ticketDetails?.winstatus?.lowercase()?.contains("sold") == true) {
                                                        temporaryList.add(ticketList)
                                                    }
                                                }
                                                else temporaryList.add(ticketList)
                                            }
                                            "non win".contains(searchedInputText.lowercase()) || "better luck next time".contains(searchedInputText.lowercase()) -> {
                                                if (ticketList.ticketDetails?.winstatus?.lowercase()?.contains("NON-WIN".lowercase()) == true) {
                                                    temporaryList.add(ticketList)
                                                }
                                            }
                                            else ->  mAdapter.putTicketList(temporaryList)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (temporaryList.isNotEmpty()) mAdapter.putTicketList(temporaryList)
                }
                else -> {}
            }
        }
    }

    private fun getDisplayTransactionDate(strDate: String?) : String {
        return if (strDate == null)
            context?.getString(R.string.na).toString()
        else
            getFormattedDateNew(strDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDateNew(transactionDate: String) : String {
        return try {
            //2021-08-31 12:15:08 ---O/P---> Aug 31 2021, 12:15 PM
            val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd yyyy, hh:mm aa")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: context?.getString(R.string.na).toString()
        } catch (e: Exception) {
            context?.getString(R.string.na).toString()
        }
    }
}