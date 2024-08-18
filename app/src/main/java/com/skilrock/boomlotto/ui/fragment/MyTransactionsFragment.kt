package com.skilrock.boomlotto.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.MyTransactionsAdapter
import com.skilrock.boomlotto.databinding.FragmentMyTransactionsBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.MyTransactionFilterSheet
import com.skilrock.boomlotto.models.request.MyTransactionsRequest
import com.skilrock.boomlotto.models.response.MyTransactionsResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.MyTransactionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MyTransactionsFragment: BaseFragment() {

    private lateinit var binding: FragmentMyTransactionsBinding
    private lateinit var viewModel: MyTransactionViewModel
    private lateinit var transactionAdapter: MyTransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionAdapter = MyTransactionsAdapter(master)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_transactions, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements()
        setViewModel()
        setBalanceBannerInfo()
        setDateRange(getPreviousDate(30), getCurrentDate())
        setRecyclerView()
        setClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[MyTransactionViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataMyTransaction.observe(master, observerTransactions)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun setBalanceBannerInfo() {
        binding.tvBalance.text              = PlayerInfo.getPlayerTotalBalance()
        binding.tvWithdrawAbleBalance.text  = HtmlCompat.fromHtml(getString(R.string.withdrawable_amount) + " <b>" +  PlayerInfo.getPlayerWithdrawalAbleBalance() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.cardBalance.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction {
            binding.cardBalance.visibility = View.VISIBLE
            binding.cardBalance.animate().scaleX(1f).scaleY(1f).setDuration(600).withEndAction {
                callMyTransactionApi()
            }
        }
    }

    private fun callMyTransactionApi() {
        binding.rvMyTransactions.visibility = View.GONE
        binding.animationView.visibility    = View.VISIBLE
        binding.llNoData.visibility         = View.GONE
        val request = MyTransactionsRequest(fromDate = getDateForRequestParam(getPreviousDate(30)),
            toDate = getDateForRequestParam(getCurrentDate()))
        viewModel.callMyTransactionApi(request)
    }

    private fun setRecyclerView() {
        binding.rvMyTransactions.apply {
            layoutManager = LinearLayoutManager(master)
            setHasFixedSize(true)
            adapter = transactionAdapter
        }
    }

    private val observerTransactions = Observer<ResponseStatus<MyTransactionsResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val txnList = it.response.txnList
                if (txnList == null || txnList.isEmpty()) {
                    binding.rvMyTransactions.visibility = View.GONE
                    binding.animationView.visibility    = View.GONE
                    binding.llNoData.visibility         = View.VISIBLE
                } else {
                    binding.animationView.visibility    = View.GONE
                    binding.llNoData.visibility         = View.GONE
                    lifecycleScope.launch {
                        delay(200)
                        binding.rvMyTransactions.visibility = View.VISIBLE
                        transactionAdapter.setTransactionsList(txnList)
                        binding.rvMyTransactions.scheduleLayoutAnimation()
                    }
                }
            }

            is ResponseStatus.Error -> {
                binding.rvMyTransactions.visibility = View.GONE
                binding.animationView.visibility    = View.GONE
                binding.llNoData.visibility         = View.VISIBLE
                val errorMessage = master.getResponseMessage(master, WEAVER, it.errorCode)
                ErrorSheet(getString(R.string.boom_lotto_error), errorMessage, master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                binding.rvMyTransactions.visibility = View.GONE
                binding.animationView.visibility    = View.GONE
                binding.llNoData.visibility         = View.VISIBLE
                ErrorSheet(getString(R.string.boom_lotto_error), getString(it.errorMessageCode), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.pullToRefresh.setOnRefreshListener {
            binding.rvMyTransactions.visibility = View.GONE
            binding.animationView.visibility    = View.VISIBLE
            binding.llNoData.visibility         = View.GONE
            setBalanceBannerInfo()
            setDateRange(getPreviousDate(30), getCurrentDate())
            binding.pullToRefresh.isRefreshing  = false
        }
        binding.pullToRefresh.setColorSchemeResources(R.color.color_app_pink)

        binding.llTransactionContainer.setOnClickListener {  }

        binding.cardFilter.setOnClickListener {
            val bottomSheetFragment = MyTransactionFilterSheet(::getDaySelected)
            bottomSheetFragment.show(parentFragmentManager, "bottomSheetFragment")
        }

        binding.ietTransactionSearchInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                myTransactionSearchFilter(p0.toString())
            }
        })
    }

    private fun getDaySelected(startDate: String, endDate: String){
        callTransactionApi(startDate, endDate)
    }

    private fun callTransactionApi(startDate: String, endDate: String) {
        binding.rvMyTransactions.visibility = View.GONE
        binding.animationView.visibility    = View.VISIBLE
        binding.llNoData.visibility         = View.GONE
        setDateRange(startDate, endDate)
        val fromDate    = getDateForRequestParam(startDate)
        val toDate      = getDateForRequestParam(endDate)
        val request     = MyTransactionsRequest(fromDate = fromDate, toDate = toDate)
        viewModel.callMyTransactionApi(request)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateForRequestParam(strDate: String) : String {
        val input   = SimpleDateFormat("MMM dd, yyyy", if (SharedPrefUtils.getAppLanguage(master) == LANGUAGE_ENGLISH) Locale("en") else Locale("ar"))
        val output  = SimpleDateFormat("dd/MM/yyyy", if (SharedPrefUtils.getAppLanguage(master) == LANGUAGE_ENGLISH) Locale("en") else Locale("ar"))
        try {
            input.parse(strDate)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return ""
    }

    private fun setDateRange(fromDate: String, toDate: String) {
        binding.tvDateRange.text = ("$fromDate ${getString(R.string.to)} $toDate")
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.transactions), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    fun myTransactionSearchFilter(searchedInputText:String){
        val temporaryList       = ArrayList<MyTransactionsResponse.Txn?>()
        val transactionResponse = viewModel.liveDataMyTransaction.value
        transactionResponse?.let {
            when(it) {
                is ResponseStatus.Success -> {
                    it.response.let { comingResponse ->
                        comingResponse.txnList?.forEach { txnLst ->
                            when {
                                txnLst?.transactionDate?.lowercase().toString().subSequence(0, 10).contains(searchedInputText.lowercase()) || txnLst?.currency?.lowercase()?.contains(searchedInputText.lowercase()) == true || txnLst?.particular?.lowercase()?.contains(searchedInputText.lowercase()) == true || txnLst?.txnAmount?.toString()?.lowercase()?.contains(searchedInputText.lowercase()) == true || txnLst?.transactionId?.toString()?.lowercase()?.contains(searchedInputText.lowercase()) == true -> {
                                    if (txnLst != null) temporaryList.add(txnLst)
                                }
                                searchedInputText.contains("+") -> {
                                    if (txnLst?.particular?.lowercase()?.contains("win") == true) temporaryList.add(txnLst)
                                }
                                searchedInputText.contains("-") -> {
                                    if (txnLst?.particular?.lowercase()?.contains("wag") == true) temporaryList.add(txnLst)
                                }
                                searchedInputText.lowercase().contains("pm") -> {
                                    val txn = txnLst?.transactionDate?.subSequence(11, 12)?.toString()
                                    if (txn != null && txn.isDigitsOnly() && txn.toInt() > 12) temporaryList.add(txnLst)
                                }
                                searchedInputText.lowercase().contains("am") -> {
                                    val txn = txnLst?.transactionDate?.subSequence(11, 12)?.toString()
                                    if(txn != null && txn.isDigitsOnly() && txn.toInt() <= 12) temporaryList.add(txnLst)
                                }
                                else -> transactionAdapter.setTransactionsList(temporaryList)
                            }
                        }
                    }
                    if (temporaryList.isNotEmpty()) transactionAdapter.setTransactionsList(temporaryList)
                }
                else -> {}
            }
        }
    }
}
