package com.skilrock.boomlotto.ui.fragment

import android.annotation.SuppressLint
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
import com.skilrock.boomlotto.adapters.InstantGamesTicketsAdapter
import com.skilrock.boomlotto.databinding.FragmentInstantGamesTicketsBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.response.InstantGameTicketListResponse
import com.skilrock.boomlotto.utility.CMS
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.viewmodels.MyTicketsViewModel
import java.text.SimpleDateFormat

class InstantGamesTicketsFragment : BaseFragment() {

    private lateinit var binding: FragmentInstantGamesTicketsBinding
    private lateinit var viewModel: MyTicketsViewModel
    private lateinit var mAdapter: InstantGamesTicketsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = InstantGamesTicketsAdapter(master)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_instant_games_tickets, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setRecyclerView()
        callTicketListApi()
        setListener()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[MyTicketsViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataInstantGameTicketList.observe(master, observerGames)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun setRecyclerView() {
        binding.rvInstantTickets.apply {
            layoutManager = LinearLayoutManager(master)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun setListener() {
        binding.pullToRefresh.setOnRefreshListener {
            binding.rvInstantTickets.visibility = View.GONE
            binding.animationView.visibility    = View.VISIBLE
            binding.llNoData.visibility         = View.GONE
            callTicketListApi()
            binding.pullToRefresh.isRefreshing  = false
        }
        binding.pullToRefresh.setColorSchemeResources(R.color.color_app_pink)

        binding.ietTicketInstantGamesSearchInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                ticketInstantGamesSearchFilter(p0.toString())
            }
        })
    }

    private val observerGames = Observer<ResponseStatus<InstantGameTicketListResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val responseData = it.response.responseData
                if (responseData == null || responseData.isEmpty()) {
                    binding.rvInstantTickets.visibility = View.GONE
                    binding.animationView.visibility    = View.GONE
                    binding.llNoData.visibility         = View.VISIBLE
                } else {
                    val ticketList = ArrayList<InstantGameTicketListResponse.ResponseData.Ticket?>()
                    responseData.forEach { gameData ->
                        if (gameData != null) {
                            gameData.ticketList?.forEach { ticket ->
                                ticket?.gameDisplayName = gameData.gameName ?: getString(R.string.na)
                                ticketList.add(ticket)
                            }
                        }
                    }

                    if (ticketList.isNotEmpty()) {
                        binding.rvInstantTickets.visibility = View.VISIBLE
                        binding.animationView.visibility    = View.GONE
                        binding.llNoData.visibility         = View.GONE
                        mAdapter.putTicketList(ticketList)
                    } else {
                        binding.rvInstantTickets.visibility = View.GONE
                        binding.animationView.visibility    = View.GONE
                        binding.llNoData.visibility         = View.VISIBLE
                    }
                }
            }

            is ResponseStatus.Error -> {
                binding.rvInstantTickets.visibility = View.GONE
                binding.animationView.visibility    = View.GONE
                binding.llNoData.visibility         = View.VISIBLE
                if (it.errorCode != 1003) {
                    val errorMessage = master.getResponseMessage(master, CMS, it.errorCode)
                    ErrorSheet(getString(R.string.boom_lotto_error), errorMessage, master.getString(R.string.close)) { master.onBackPressed() }.apply {
                        show(master.supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }

            is ResponseStatus.TechnicalError -> {
                binding.rvInstantTickets.visibility = View.GONE
                binding.animationView.visibility    = View.GONE
                binding.llNoData.visibility         = View.VISIBLE
                ErrorSheet(getString(R.string.boom_lotto_error), getString(it.errorMessageCode), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {

    }

    private fun callTicketListApi() {
        viewModel.callInstantGameTicketListApi()

    }

    fun ticketInstantGamesSearchFilter(searchedInputText:String) {
        val temporaryList       = ArrayList<InstantGameTicketListResponse.ResponseData.Ticket?>()
        val transactionResponse = viewModel.liveDataInstantGameTicketList.value
        transactionResponse?.let {
            when(it) {
                is ResponseStatus.Success -> {
                    it.response.responseData?.forEach { responseData ->
                        if (responseData != null) {
                            responseData.ticketList?.forEach { ticket ->
                                if (ticket != null) {
                                    when {
                                        ticket.gameDisplayName.lowercase().contains(searchedInputText.lowercase()) -> {
                                            temporaryList.add(ticket)
                                        }
                                        getDisplayTransactionDate(ticket.transactionDate).lowercase().contains(searchedInputText.lowercase()) -> {
                                            temporaryList.add(ticket)
                                        }
                                        ( "#" + ticket.ticketNumber).lowercase().contains(searchedInputText.lowercase()) -> {
                                            temporaryList.add(ticket)
                                        }
                                        "${ticket.ticketDetails?.txnCurrency.toString()} ${ticket.ticketDetails?.saleAmount.toString()}".lowercase().contains(searchedInputText.lowercase()) -> {
                                            temporaryList.add(ticket)
                                        }
                                        "${ticket.ticketDetails?.txnCurrency.toString()} ${ticket.ticketDetails?.winningAmount.toString()}".lowercase().contains(searchedInputText.lowercase()) -> {
                                            temporaryList.add(ticket)
                                        }
                                        "mabrook!".contains(searchedInputText.lowercase()) || "you won".contains(searchedInputText.lowercase()) || "winning".contains(searchedInputText.lowercase()) || "claimed".contains(searchedInputText.lowercase()) -> {
                                            if ((ticket.ticketDetails?.winstatus.toString().lowercase().contains("claimed")) || (ticket.ticketDetails?.winstatus.toString().lowercase().contains("win")) ) {
                                                temporaryList.add(ticket)
                                            }
                                        }
                                        ticket.ticketDetails?.winstatus.toString().lowercase().contains(searchedInputText.lowercase()) -> {
                                           if ( !((ticket.ticketDetails?.winstatus.toString().lowercase().contains("win")) || (ticket.ticketDetails?.winstatus.toString().lowercase().contains("claimed"))) ) {
                                               temporaryList.add(ticket)
                                           }
                                        }
                                        else -> mAdapter.putTicketList(temporaryList)
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
            getFormattedDate(strDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(transactionDate: String) : String {
        return try {
            //2021-08-31 12:15:08
            val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd yyyy, hh:mm aa")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: context?.getString(R.string.na).toString()
        } catch (e: Exception) {
            context?.getString(R.string.na).toString()
        }
    }

}