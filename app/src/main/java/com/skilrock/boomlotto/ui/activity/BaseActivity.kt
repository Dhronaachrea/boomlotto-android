package com.skilrock.boomlotto.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.NoInternetSheet
import com.skilrock.boomlotto.dialogs.SessionExpirySheet
import com.skilrock.boomlotto.models.response.HeaderInfoResponse
import com.skilrock.boomlotto.utility.*
import kotlinx.android.synthetic.main.activity_home.*

abstract class BaseActivity : AppCompatActivity() {

    abstract fun setDataBindingAndViewModel()
    abstract fun hideKeyboard()
    lateinit var headerInfoListener: HeaderInfoListener

    fun setWindowStyling() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
        supportActionBar?.hide()
    }

    fun setLanguage() {
        if (SharedPrefUtils.getAppLanguage(this) == LANGUAGE_ENGLISH) {
            LocaleHelper.setLanguage(this, LANGUAGE_ENGLISH)
        } else {
            LocaleHelper.setLanguage(this, LANGUAGE_ARABIC)
        }
    }

    val observerVibrator = Observer<String> { vibrate(this) }

    val observerLoader = Observer<Boolean> {
        if (it) {
            progressBar.animate().alpha(1f).setDuration(50).withEndAction {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                progressBar.visibility = View.VISIBLE
            }
        }
        else {
            progressBar.animate().alpha(0f).setDuration(50).withEndAction {
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    val observerHideKeyboard = Observer<Boolean> {
        if (it) hideKeyboard()
    }

    fun getResponseMessage(context: Context, tag: String, responseCode: Int): String {
        if (responseCode == -1)
            return context.getString(R.string.unable_to_fetch_status)

        val message: String = try {
            val name = tag + "_" + responseCode
            val id = context.resources.getIdentifier(name, "string", context.packageName)
            context.getString(id)
        } catch (e: Exception) {
            context.getString(R.string.unable_to_fetch_information) + " [${tag.take(1).uppercase()}" + responseCode + "]"
        }
        return message
    }

    val observerHeaderInfo = Observer<ResponseStatus<HeaderInfoResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val response: HeaderInfoResponse = it.response
                response.unreadMsgCount?.let { unreadCount -> PlayerInfo.setBadgeCount(this, unreadCount) }
                PlayerInfo.setBalance(this, response)
                if (this::headerInfoListener.isInitialized)
                    headerInfoListener.onHeaderInfoApiResponseCallback()
            }

            is ResponseStatus.Error -> {
                val errorMessage = getResponseMessage(this, WEAVER, it.errorCode)
                Log.e("log", "Header Info Api Error: $errorMessage")
                if (WEAVER_SESSION_EXPIRY_CODE == it.errorCode) {
                    SessionExpirySheet( { performLogoutCleanUp(this, Intent(this, HomeActivity::class.java)) } ) {
                        startActivity(Intent(this, LoginActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }.apply {
                        show(supportFragmentManager, SessionExpirySheet.TAG)
                    }
                }
            }

            is ResponseStatus.TechnicalError -> Log.e("log", "Header Info API Error: ${getString(it.errorMessageCode)}")
        }
    }

    val observerNetworkError = Observer<ResponseStatusError> {
        when (it) {
            is ResponseStatusError.NoInternetError -> {
                NoInternetSheet().apply {
                    show(supportFragmentManager, NoInternetSheet.TAG)
                }
            }

            is ResponseStatusError.TechnicalError -> {
                ErrorSheet(getString(R.string.boom_lotto_error), getString(it.errorMessageCode), getString(R.string.close)) {  }.apply {
                    show(supportFragmentManager, ErrorSheet.TAG)
                }
            }
        }
    }

    interface HeaderInfoListener {
        fun onHeaderInfoApiResponseCallback()
    }
}