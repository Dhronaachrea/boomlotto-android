package com.skilrock.boomlotto.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.ResultAdapter
import com.skilrock.boomlotto.databinding.FragmentResultBinding
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.ResultFilterSheet
import com.skilrock.boomlotto.models.response.ResultResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.ResultViewModel
import java.text.SimpleDateFormat

class ResultFragment : BaseFragment() {

    private lateinit var binding: FragmentResultBinding
    private lateinit var viewModel: ResultViewModel
    private lateinit var mAdapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = ResultAdapter(master)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements()
        setViewModel()
        callResultApi()
        setDateRange(getPreviousDate(30), getCurrentDate())
        setRecyclerView()
        setClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[ResultViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(master, observerVibrator)
        viewModel.liveDataLoader.observe(master, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataResult.observe(master, observerResult)
        viewModel.liveDataNetworkError.observe(master, observerNetworkError)
    }

    private fun callResultApi() {
        binding.rvResult.visibility         = View.GONE
        binding.animationView.visibility    = View.VISIBLE
        binding.llNoData.visibility         = View.GONE
        viewModel.callResultApi(viewModel.getDateForRequestParam(getPreviousDate(30)), viewModel.getDateForRequestParam(getCurrentDate()))
    }

    private fun setRecyclerView() {
        binding.rvResult.apply {
            layoutManager = LinearLayoutManager(master)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private val observerResult = Observer<ResponseStatus<ResultResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val boomGame: ResultResponse.ResponseData? = it.response.responseData?.find { data -> data?.gameCode == BOOM_GAME_CODE }
                val resultList = boomGame?.resultData

                if (resultList != null && resultList.isNotEmpty()) {
                    prepareDataForRecyclerView(resultList)
                } else {
                    binding.rvResult.visibility         = View.GONE
                    binding.animationView.visibility    = View.GONE
                    binding.llNoData.visibility         = View.VISIBLE
                }
            }

            is ResponseStatus.Error -> {
                binding.rvResult.visibility         = View.GONE
                binding.animationView.visibility    = View.GONE
                binding.llNoData.visibility         = View.VISIBLE
                if (it.errorCode != 1016) {
                    val errorMessage = master.getResponseMessage(master, WEAVER, it.errorCode)
                    ErrorSheet(getString(R.string.boom_lotto_error), errorMessage, master.getString(R.string.close)) { master.onBackPressed() }.apply {
                        show(master.supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }

            is ResponseStatus.TechnicalError -> {
                binding.rvResult.visibility         = View.GONE
                binding.animationView.visibility    = View.GONE
                binding.llNoData.visibility         = View.VISIBLE
                ErrorSheet(getString(R.string.boom_lotto_error), getString(it.errorMessageCode), master.getString(R.string.close)) { master.onBackPressed() }.apply {
                    show(master.supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private fun prepareDataForRecyclerView(resultList: ArrayList<ResultResponse.ResponseData.ResultData?>) {
        val listResults: ArrayList<ResultResponse.ResponseData.ResultData.ResultInfo> = ArrayList()
        resultList.forEach { resultData: ResultResponse.ResponseData.ResultData? ->
            resultData?.resultInfo?.forEach { resultInfo: ResultResponse.ResponseData.ResultData.ResultInfo? ->
                resultInfo?.let {
                    it.resultDate = resultData.resultDate
                    listResults.add(it)
                }
            }
        }

        if (listResults.isNotEmpty()) {
            listResults.forEachIndexed { index, resultInfo -> resultInfo.rowIndex = index }
            binding.rvResult.visibility         = View.VISIBLE
            binding.animationView.visibility    = View.GONE
            binding.llNoData.visibility         = View.GONE
            mAdapter.setResultListData(listResults)
            binding.rvResult.scheduleLayoutAnimation()
        } else {
            binding.rvResult.visibility         = View.GONE
            binding.animationView.visibility    = View.GONE
            binding.llNoData.visibility         = View.VISIBLE
        }
    }

    private fun setClickListeners() {
        binding.llResultContainer.setOnClickListener {  }

        binding.cardFilter.setOnClickListener {
            val bottomSheetFragment = ResultFilterSheet(::getDaySelected)
            bottomSheetFragment.show(parentFragmentManager, "bottomSheetFragment")
        }

        binding.ietResultSearchInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                resultSearchFilter(p0.toString())
            }
        })
    }

    private fun getDaySelected(startDate: String, endDate: String){
        binding.rvResult.visibility         = View.GONE
        binding.animationView.visibility    = View.VISIBLE
        binding.llNoData.visibility         = View.GONE
        setDateRange(startDate, endDate)
        viewModel.callResultApi(viewModel.getDateForRequestParam(startDate),viewModel.getDateForRequestParam(endDate))
    }

    private fun setDateRange(fromDate: String, toDate: String) {
        binding.tvDateRange.text = ("$fromDate ${getString(R.string.to)} $toDate")
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.results), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }

    fun resultSearchFilter(searchedInputText:String) {
        val temporaryList       = ArrayList<ResultResponse.ResponseData.ResultData.ResultInfo>()
        val transactionResponse = viewModel.liveDataResult.value
        transactionResponse?.let {
            when(it) {
                is ResponseStatus.Success -> {
                    val boomGame: ResultResponse.ResponseData? = it.response.responseData?.find { data -> data?.gameCode == BOOM_GAME_CODE }
                    val resultList = boomGame?.resultData
                    if (resultList != null && resultList.isNotEmpty()) {
                        resultList.forEach { resultData: ResultResponse.ResponseData.ResultData? ->
                            if (resultData != null) {
                                resultData.resultInfo?.forEach { resultInfo: ResultResponse.ResponseData.ResultData.ResultInfo? ->
                                    if (resultInfo != null) {
                                        when {
                                            getDayName(resultData.resultDate.toString()).lowercase().contains(searchedInputText.lowercase()) -> {
                                                temporaryList.add(resultInfo)
                                            }
                                            ("${getNewAbbreviatedFromDate(resultData.resultDate.toString())}, ${getAbbreviatedFromTime(resultInfo.drawTime.toString())}").lowercase().contains(searchedInputText.lowercase()) -> {
                                                temporaryList.add(resultInfo)
                                            }
                                            resultInfo.drawId.toString().contains(searchedInputText.lowercase()) -> {
                                                temporaryList.add(resultInfo)
                                            }
                                            resultInfo.winningNo.toString().contains(searchedInputText.lowercase()) -> {
                                                temporaryList.add(resultInfo)
                                            }
                                            else -> mAdapter.setResultListData(temporaryList)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (temporaryList.isNotEmpty()) mAdapter.setResultListData(temporaryList)
                }
                else ->{}
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDayName(dateTime: String): String {
        val input   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output  = SimpleDateFormat("EEEE")
        try {
            input.parse(dateTime)?.let {
                return output.format(it).toString()
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return requireContext().getString(R.string.na)
    }

    @SuppressLint("SimpleDateFormat")
    fun getNewAbbreviatedFromDate(dateTime: String): String? {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output = SimpleDateFormat("MMM dd yyyy")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return requireContext().getString(R.string.na)
    }

    @SuppressLint("SimpleDateFormat")
    fun getAbbreviatedFromTime(dateTime: String): String? {
        val input   = SimpleDateFormat("HH:mm:ss")
        val output  = SimpleDateFormat("hh:mm aa")
        try {
            input.parse(dateTime)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return requireContext().getString(R.string.na)
    }
}