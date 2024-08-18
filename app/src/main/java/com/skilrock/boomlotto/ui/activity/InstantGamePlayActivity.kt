package com.skilrock.boomlotto.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.ActivityInstantGamePlayBinding
import com.skilrock.boomlotto.utility.showToast

class InstantGamePlayActivity : BaseActivity() {

    private lateinit var binding: ActivityInstantGamePlayBinding
    private var clearHistory = false
    private var mUrl = ""
    //private var mGameCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setDataBindingAndViewModel()
        setClickListeners()
        setWebViewElements()
    }

    override fun setDataBindingAndViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_instant_game_play)
    }

    private fun setWebViewElements() {
        intent.getStringExtra("gameUrl")?.let {
            mUrl = it
            initWebView()
            setWebClient()
            //val url = getCompleteUrl()
            //Log.w("log", "Complete Url: $url")
            binding.webView.loadUrl(mUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webView.settings.setSupportMultipleWindows(true)
        binding.webView.settings.javaScriptEnabled      = true
        binding.webView.settings.loadWithOverviewMode   = true
        binding.webView.settings.useWideViewPort        = true
        binding.webView.settings.domStorageEnabled      = true
        binding.webView.settings.userAgentString        = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36"
        //binding.webView.settings.userAgentString      = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
        //binding.webView.settings.userAgentString      = System.getProperty("http.agent")

        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.setInitialScale(1)

        binding.webView.webViewClient = object : WebViewClient() {
            override
            fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }

        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.addJavascriptInterface(JavaScriptInterface(), "JSInterface")
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

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?
            ): Boolean {
                Log.w("onJsAlert", "URL: $url")
                Log.w("onJsAlert", "Message: $message")
                return false
            }
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(webView: WebView, url: String?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("log", "onPageFinished: $url")
                if (clearHistory) {
                    clearHistory = false
                    binding.webView.clearHistory()
                }
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                //Log.d("log", "logonLoadResource: $url")
                super.onLoadResource(view, url)
            }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        finish()
        return super.onKeyDown(keyCode, event)
    }

    inner class JavaScriptInterface {

        @JavascriptInterface
        fun showLoginDialog() {
            Log.w("log", "Open Login Dialog")
            openLoginDialog()
        }

        @JavascriptInterface
        fun onBalanceUpdate() {
            Log.w("log", "onBalanceUpdate()")
        }

        @JavascriptInterface
        fun updateBalance() {
            Log.w("log", "updateBalance()")
        }

        @JavascriptInterface
        fun informSessionExpiry() {
            showToast(getString(R.string.session_expired))
            openLoginDialog()
        }

        @JavascriptInterface
        fun setToolbarTitle(title: String) {

        }

        @JavascriptInterface
        fun goToHome() {
            finish()
        }

        @JavascriptInterface
        fun loadInstantGameUrl(igeUrl: String) {
            val finalUrl = "$igeUrl&isNative=android"
            Log.i("log", "Instant Game URL: $finalUrl")
            binding.webView.post {
                binding.webView.loadUrl(finalUrl)
            }
        }

        @JavascriptInterface
        fun makeWebViewVisible() {
            //makeWebViewVisible
        }

        @JavascriptInterface
        fun loginWindow() {
            Log.e("log", "loginWindow(): Open Login Dialog")
            openLoginDialog()
        }

        @JavascriptInterface
        fun backToLobby() {
            Log.w("log", "backToLobby()")
            finish()
        }

        @JavascriptInterface
        fun backtoLobby() {
            Log.w("log", "backToLobby()")
            finish()
        }

        @JavascriptInterface
        fun updateBal() {
            Log.w("log", "updateBal()")
        }

        @JavascriptInterface
        fun reloadGame(tag: String) {
            if (tag == "appGameLoad") {
                binding.webView.post {
                    val url = mUrl
                    Log.w("log", "Reload Complete Url: $url")
                    binding.webView.loadUrl(url)
                    clearHistory = true
                }
            }
        }

    }

    private fun openLoginDialog() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun setClickListeners() {

    }

    /*private fun getCompleteUrl() : String {
        //:baseUrl/vendorid/:gameid/:playType/:playerid/:playername/:sessid/:bal/:lang/:curr/:currDisplay/:alias/:isMobileApp
        //
        //Login : https://dubai-games.lottoweaver.com/ige/DUBAI/101/buy/411187/971789456/qnSdiEadKWBJl2aJdvMPSK0QZC2vZm-egdsidcmBkPg/98560.0/en/AED/AED/www.winboom.ae/1
        //
        //Guest : https://dubai-games.lottoweaver.com/ige/DUBAI/101/buy/-/-/-/0/en/AED/AED/www.winboom.ae/1

        return if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            "${mUrl}/ige/DUBAI/${mGameCode}/buy/-/-/-/0/${SharedPrefUtils.getAppLanguage(this)}/${PlayerInfo.getCurrency()}/${PlayerInfo.getCurrencyDisplayCode()}/${BuildConfig.DOMAIN_NAME}/1"
        } else {
            "${mUrl}/ige/DUBAI/${mGameCode}/buy/${PlayerInfo.getPlayerId()}/${PlayerInfo.getPlayerUserName()}/${PlayerInfo.getPlayerToken()}/${PlayerInfo.getRawBalance()}/${SharedPrefUtils.getAppLanguage(this)}/${PlayerInfo.getCurrency()}/${PlayerInfo.getCurrencyDisplayCode()}/${BuildConfig.DOMAIN_NAME}/1"
        }
    }*/

    override fun hideKeyboard() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}