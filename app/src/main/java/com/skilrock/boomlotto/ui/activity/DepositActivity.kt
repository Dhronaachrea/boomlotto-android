package com.skilrock.boomlotto.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.DepositPendingAdapter
import com.skilrock.boomlotto.databinding.ActivityDepositBinding
import com.skilrock.boomlotto.dialogs.DepositSuccessSheet
import com.skilrock.boomlotto.dialogs.DepositTransactionFailedSheet
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.models.response.BonusResponse
import com.skilrock.boomlotto.models.response.DepositPendingResponse
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.DepositViewModel
import kotlinx.android.synthetic.main.toolbar.view.*
import org.json.JSONObject


class DepositActivity : BaseActivity(), BaseActivity.HeaderInfoListener {

    private lateinit var binding: ActivityDepositBinding
    private lateinit var viewModel: DepositViewModel
    private var mPaymentOptions: PaymentOptionsResponse? = null
    private var mTxnId: Long = -1
    private var mTxnDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowStyling()
        setDataBindingAndViewModel()
        callHeaderInfoApi()
        setCurrency()
        callPaymentOptionsApi()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        setToolbarElements()
    }

    override fun setDataBindingAndViewModel() {
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_deposit)
        viewModel   = ViewModelProvider(this)[DepositViewModel::class.java]

        binding.lifecycleOwner  = this
        binding.viewModel       = viewModel
        headerInfoListener      = this

        binding.toolbar.tbNavigationIcon.setImageResource(R.drawable.icon_back)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataPaymentOptions.observe(this, observerPaymentOptions)
        viewModel.liveDataPendingResponse.observe(this, observerDepositPendingResponse)
        viewModel.liveDataNetworkError.observe(this, observerNetworkError)
        viewModel.liveDataBonus.observe(this, observeBonus)
    }

    private fun callHeaderInfoApi() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isNotEmpty()) {
            viewModel.callHeaderInfoApi()
        }
    }

    private fun setCurrency() {
        binding.tvCurrency.text = PlayerInfo.getCurrencyDisplayCode()
        binding.tvAmount1.postDelayed( { binding.tvAmount1.performClick() }, 300)
    }

    private fun callPaymentOptionsApi() {
        viewModel.callPaymentOptionsApi()
    }

    private fun setListeners() {
        binding.toolbar.llDrawerIcon.setOnClickListener { onBackPressed() }

        binding.etAmount.afterTextChanged {
            binding.tvError.text            = ""
            binding.tvError.visibility      = View.GONE
            binding.llAmountBox.background  = ContextCompat.getDrawable(this, R.drawable.deposit_amount_outline)
            amountBoxOperation(null)

            try {
                val amount = it.toDouble()
                binding.btnProceed.isActivated = amount > 0
            } catch (e: Exception) {
                binding.btnProceed.isActivated = false
            }
        }

        binding.etAmount.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (binding.btnProceed.isEnabled)
                        binding.btnProceed.performClick()
                    true
                }
                else -> false
            }
        }

        binding.btnProceed.setOnClickListener {
            if (validate()) {
                mPaymentOptions?.listPayTypeMap?.let {
                    if (it.isNotEmpty()) {
                        hideKeyboard()
                        val formData = viewModel.getFormData(it[0], binding.etAmount.getTrimText())
                        Log.e("log", "Form Data: $formData")
                        val intentDeposit = Intent(this, DepositPaymentActivity::class.java)
                        intentDeposit.putExtra("requestData", formData)
                        startActivityForResult.launch(intentDeposit)
                    } else {
                        ErrorSheet(getString(R.string.deposit_error), getString(R.string.something_went_wrong), getString(R.string.close)) { onBackPressed() }.apply {
                            show(supportFragmentManager, ErrorSheet.TAG)
                        }
                    }
                } ?: run {
                    ErrorSheet(getString(R.string.deposit_error), getString(R.string.some_internal_error), getString(R.string.close)) { onBackPressed() }.apply {
                        show(supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }
        }

        binding.tvAmount1.setOnClickListener { amountBoxOperation(binding.tvAmount1) }
        binding.tvAmount2.setOnClickListener { amountBoxOperation(binding.tvAmount2) }
        binding.tvAmount3.setOnClickListener { amountBoxOperation(binding.tvAmount3) }
        binding.tvAmount4.setOnClickListener { amountBoxOperation(binding.tvAmount4) }
    }

    private fun amountBoxOperation(textView: MaterialTextView? = null) {
        binding.tvAmount1.background = ContextCompat.getDrawable(this, R.drawable.pre_deposit_amount_unselected_box)
        binding.tvAmount2.background = ContextCompat.getDrawable(this, R.drawable.pre_deposit_amount_unselected_box)
        binding.tvAmount3.background = ContextCompat.getDrawable(this, R.drawable.pre_deposit_amount_unselected_box)
        binding.tvAmount4.background = ContextCompat.getDrawable(this, R.drawable.pre_deposit_amount_unselected_box)

        binding.tvAmount1.setTextColor(ContextCompat.getColor(this, R.color.color_app_base_blue))
        binding.tvAmount2.setTextColor(ContextCompat.getColor(this, R.color.color_app_base_blue))
        binding.tvAmount3.setTextColor(ContextCompat.getColor(this, R.color.color_app_base_blue))
        binding.tvAmount4.setTextColor(ContextCompat.getColor(this, R.color.color_app_base_blue))

        textView?.apply {
            binding.etAmount.setText(text)
            binding.etAmount.setSelection(binding.etAmount.text.toString().length)
            setTextColor(ContextCompat.getColor(this@DepositActivity, R.color.color_app_pink))
            background = ContextCompat.getDrawable(this@DepositActivity, R.drawable.pre_deposit_amount_selected_box)
        }
    }

    override fun hideKeyboard() {
        binding.btnProceed.hideKeyboard()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun setToolbarElements() {
        binding.toolbar.llToolbarNotification.visibility    = View.VISIBLE
        binding.toolbar.tvBalance.text                      = HtmlCompat.fromHtml(PlayerInfo.getPlayerTotalBalanceBold(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.toolbar.tvToolbarLabel.text                 = getString(R.string.deposit)
        binding.toolbar.tvToolbarLabel.visibility           = View.VISIBLE
        binding.toolbar.ivToolBarIcon.visibility            = View.GONE

        if (PlayerInfo.getBadgeCount(this) > 0) {
            if (PlayerInfo.getBadgeCount(this) < 10) {
                binding.toolbar.tvBadgeCount.text       = PlayerInfo.getBadgeCount(this).toString()

            } else {
                binding.toolbar.tvBadgeCount.text       = "9+"
            }
            binding.toolbar.tvBadgeCount.visibility     = View.VISIBLE

        } else {
            binding.toolbar.tvBadgeCount.visibility     = View.GONE
        }
    }

    private val observerPaymentOptions = Observer<ResponseStatus<JSONObject>> { responseStatus ->
        viewModel.callDepositPendingApi()
        when (responseStatus) {
            is ResponseStatus.Success -> {
                mPaymentOptions = PaymentOptionsResponseParser().handleResponse(responseStatus.response)
            }

            is ResponseStatus.Error -> {
                val errorMessage = getResponseMessage(this, CASHIER, responseStatus.errorCode)
                ErrorSheet(getString(R.string.deposit_error), errorMessage, getString(R.string.close)) { onBackPressed() }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorSheet(getString(R.string.deposit_error), getString(responseStatus.errorMessageCode), getString(R.string.close)) { onBackPressed() }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    private val observerDepositPendingResponse = Observer<ResponseStatus<DepositPendingResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val response: DepositPendingResponse = it.response
                response.txnList?.let { list ->

                    if (list.size > 0) {
                        val animation = TranslateAnimation(-800.0f, 0.0f, 0.0f, 0.0f)
                        animation.duration      = 400
                        animation.repeatCount   = 0
                        animation.fillAfter     = false
                        binding.tvPendingLabel.startAnimation(animation)

                        binding.llPendingTransactions.visibility = View.VISIBLE
                        binding.rvPendingDeposit.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = DepositPendingAdapter(list)
                        }
                    } else {
                        binding.llPendingTransactions.visibility = View.GONE
                    }
                } ?: run {
                    binding.llPendingTransactions.visibility = View.GONE
                }
            }

            is ResponseStatus.Error -> {
                binding.llPendingTransactions.visibility = View.GONE
                showToast(getResponseMessage(this, CASHIER, it.errorCode))
            }

            is ResponseStatus.TechnicalError -> binding.llPendingTransactions.visibility = View.GONE

        }
    }

    private fun validate() : Boolean {
        try {
            if (binding.etAmount.getTrimText().isBlank() || binding.etAmount.getTrimText().toDouble() <= 0) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = getString(R.string.enter_valid_amount)
                binding.llAmountBox.startAnimation(shakeError())
                binding.llAmountBox.background = ContextCompat.getDrawable(this, R.drawable.login_country_view_outline_error)
                return false
            }
        } catch (e: Exception) {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = getString(R.string.enter_valid_amount)
            binding.llAmountBox.startAnimation(shakeError())
            binding.llAmountBox.background = ContextCompat.getDrawable(this, R.drawable.login_country_view_outline_error)
            return false
        }

        return true
    }

    private val startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 222) {
            val responseData: String? = result.data?.getStringExtra("responseData")
            responseData?.let { response -> handleResponse(response) } ?: this showToast getString(R.string.some_problem_occurred)
        }
    }

    private fun handleResponse(response: String) {
        /*{
            "txnId": "4859",
            "netAmount": 50.0,
            "status": "SUCCESS",
            "txnType": "DEPOSIT",
            "responseUrl": "MOBILE_APP",
            "firstDeposit": false,
            "txnDate": "2021-11-13 11:52:59",
            "domainName": "www.winboom.ae",
            "userName": "97155555555",
            "merchantTxnId": "3863410",
            "errorCode": 0,
            "respMsg": "Deposit Request Successfully Done..."
        }*/
        try {
            val jsonObject          = JSONObject(response)
            val status: String?     = jsonObject.optString("status")
            val respMsg: String?    = jsonObject.optString("respMsg")
            val merchantTxnId: Long = if (jsonObject.has("merchantTxnId")) jsonObject.getLong("merchantTxnId") else -1

            if (status != null && status.equals("SUCCESS", true)) {
                mTxnId      = if (jsonObject.has("txnId")) jsonObject.getLong("txnId") else merchantTxnId
                mTxnDate    = if (jsonObject.has("txnDate")) jsonObject.getString("txnDate") else ""

                if (merchantTxnId != (-1).toLong())
                    viewModel.callBonusApi(merchantTxnId)
                else {
                    DepositSuccessSheet(false, binding.etAmount.getTrimText(), "0", mTxnId.toString(), "-", ::onDepositSuccessSheetClose, ::onPlayBoomFive).apply {
                        show(supportFragmentManager, DepositSuccessSheet.TAG)
                    }
                }
            } else {
                var message = ""
                if (respMsg != null && respMsg.isNotBlank())
                    message = respMsg

                DepositTransactionFailedSheet(message, binding.etAmount.getTrimText()).apply {
                    show(supportFragmentManager, DepositTransactionFailedSheet.TAG)
                }
            }
        } catch (e: Exception) {
            Log.e("log", e.message ?: "ERROR")
            DepositTransactionFailedSheet("", binding.etAmount.getTrimText()).apply {
                show(supportFragmentManager, DepositTransactionFailedSheet.TAG)
            }
        }
    }

    private val observeBonus = Observer<ResponseStatus<BonusResponse>> {
        var bonusAmount = 0.0
        when(it){
            is ResponseStatus.Success -> {
                val dataList: List<BonusResponse.Data?>? = it.response.data
                if(dataList != null && dataList.isNotEmpty()) bonusAmount = dataList[0]?.receivedBonus ?: 0.0
            }

            is ResponseStatus.Error -> {
                Log.e("OkHttp", "Error: $it")
            }

            is ResponseStatus.TechnicalError -> {
                Log.e("OkHttp", "Technical Error: $it")
            }
        }

        if (bonusAmount > 0) {
            DepositSuccessSheet(true, binding.etAmount.getTrimText(), bonusAmount.toString(), mTxnId.toString(), mTxnDate, ::onDepositSuccessSheetClose, ::onPlayBoomFive).apply {
                show(supportFragmentManager, DepositSuccessSheet.TAG)
            }
        } else {
            DepositSuccessSheet(false, binding.etAmount.getTrimText(), "0", mTxnId.toString(), mTxnDate, ::onDepositSuccessSheetClose, ::onPlayBoomFive).apply {
                show(supportFragmentManager, DepositSuccessSheet.TAG)
            }
        }
    }

    override fun onHeaderInfoApiResponseCallback() {
        setToolbarElements()
    }

    private fun onDepositSuccessSheetClose() {
        onBackPressed()
    }

    private fun onPlayBoomFive() {
        startActivity(Intent(this, BoomLottoActivity::class.java))
        finish()
    }
}