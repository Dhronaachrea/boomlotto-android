package com.skilrock.boomlotto.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.ActivityDepositWithdrawalPaymentBinding
import com.skilrock.boomlotto.models.response.HeaderInfoResponse
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.ResponseStatus
import com.skilrock.boomlotto.viewmodels.DepositWithdrawalPaymentViewModel
import kotlinx.android.synthetic.main.toolbar.view.*

class DepositPaymentActivity : BaseActivity(), BaseActivity.HeaderInfoListener {

    private lateinit var viewModel: DepositWithdrawalPaymentViewModel
    private lateinit var binding : ActivityDepositWithdrawalPaymentBinding
    private var mDepositWebResponseData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDataBindingAndViewModel()
        setClickListeners()
        setWebViewElements()
    }

    override fun onResume() {
        super.onResume()
        setToolbarElements()
    }

    override fun setDataBindingAndViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deposit_withdrawal_payment)

        viewModel               = ViewModelProvider(this)[DepositWithdrawalPaymentViewModel::class.java]
        binding.lifecycleOwner  = this
        binding.viewModel       = viewModel
        headerInfoListener      = this

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataHeaderInfo.observe(this, observerHeaderInfo)
        viewModel.liveDataHeaderInfoCallback.observe(this, observerHeaderInfoCallback)
    }

    override fun hideKeyboard() {

    }

    private fun setWebViewElements() {
        intent.getStringExtra("requestData")?.let { requestData ->

            initWebView()
            setWebClient()
            binding.webView.loadData(requestData, "text/html", "UTF-8")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webView.settings.javaScriptEnabled      = true
        binding.webView.settings.loadWithOverviewMode   = true
        binding.webView.settings.useWideViewPort        = true
        binding.webView.settings.domStorageEnabled      = true
        binding.webView.settings.userAgentString        = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36"

        binding.webView.settings.setSupportMultipleWindows(true)
        binding.webView.setInitialScale(1)

        WebView.setWebContentsDebuggingEnabled(true)
    }

    private fun setWebClient() {
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressBar.progress = newProgress
                if (newProgress < 100 && binding.progressBar.visibility == ProgressBar.GONE) {
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                }

                if (newProgress == 100) {
                    binding.progressBar.visibility = ProgressBar.GONE
                }
            }
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                viewModel.liveDataLoader.value = true
            }

            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                binding.webView.loadUrl(url)
                return true
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                viewModel.liveDataLoader.value = false

                Log.d("log", "onPageFinished: $url")
                //binding.webView.loadUrl("javascript:window.HtmlViewer.showHTML(document.getElementsByName('responseJson')[0].value);")

                binding.webView.loadUrl("javascript:"
                        + "var len = document.getElementsByName('responseJson').length;"
                        + "if (len > 0) {"
                        + " window.HtmlViewer.showHTML(document.getElementsByName('responseJson')[0].value);"
                        + "} else {"
                        + " window.HtmlViewer.showHTML('');"
                        + " }")

            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
            }
        }

        binding.webView.addJavascriptInterface(MyJavaScriptInterface(), "HtmlViewer")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    inner class MyJavaScriptInterface {

        @JavascriptInterface
        fun showHTML(html: String?) {
            //Log.d("log", "SHOW HTML: $html")

            html?.let { data: String ->
                if (data.isNotBlank()) {
                    mDepositWebResponseData = data
                    Log.d("log", "JSON: $mDepositWebResponseData")
                    viewModel.callHeaderInfoApi("DEPOSIT")
                }
            }
        }
    }

    private val observerHeaderInfoCallback = Observer<ResponseStatus<HeaderInfoResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val response: HeaderInfoResponse = it.response
                response.unreadMsgCount?.let { unreadCount -> PlayerInfo.setBadgeCount(this, unreadCount) }
                PlayerInfo.setBalance(this, response)
                setToolbarElements()
            }

            is ResponseStatus.Error -> { }

            is ResponseStatus.TechnicalError -> { }
        }

        val intent = Intent()
        intent.putExtra("responseData", mDepositWebResponseData)
        setResult(222, intent)
        finish()
    }

    private fun setClickListeners() {
        binding.toolbar.llDrawerIcon.setOnClickListener {
            finish()
        }
    }

    fun setToolbarElements() {
        binding.toolbar.llToolbarNotification.visibility    = View.VISIBLE
        binding.toolbar.tvBalance.text                      = HtmlCompat.fromHtml(PlayerInfo.getPlayerTotalBalanceBold(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.toolbar.tvToolbarLabel.text                 = getString(R.string.deposit)
        binding.toolbar.tvToolbarLabel.visibility           = View.VISIBLE
        binding.toolbar.ivToolBarIcon.visibility            = View.GONE

        binding.toolbar.tbNavigationIcon.setImageResource(R.drawable.icon_back)
        if (PlayerInfo.getBadgeCount(this) > 0) {
            binding.toolbar.tvBadgeCount.text       = PlayerInfo.getBadgeCount(this).toString()
            binding.toolbar.tvBadgeCount.visibility = View.VISIBLE
        } else
            binding.toolbar.tvBadgeCount.visibility = View.GONE
    }

    override fun onHeaderInfoApiResponseCallback() {
        setToolbarElements()
    }
}
