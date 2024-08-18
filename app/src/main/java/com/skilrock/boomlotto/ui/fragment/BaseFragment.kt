package com.skilrock.boomlotto.ui.fragment

import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.dialogs.ErrorSheet
import com.skilrock.boomlotto.dialogs.NoInternetSheet
import com.skilrock.boomlotto.ui.activity.HomeActivity
import com.skilrock.boomlotto.utility.ResponseStatusError
import com.skilrock.boomlotto.utility.vibrate
import kotlinx.android.synthetic.main.activity_home.*

abstract class BaseFragment : Fragment() {

    lateinit var master: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity)
            master = context
    }

    val observerVibrator = Observer<String> { vibrate(master) }

    val observerLoader = Observer<Boolean> {
        if (it) {
            master.progressBar.animate().alpha(1f).setDuration(50).withEndAction {
                master.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                master.progressBar.visibility = View.VISIBLE
            }
        }
        else {
            master.progressBar.animate().alpha(0f).setDuration(50).withEndAction {
                master.progressBar.visibility = View.INVISIBLE
                master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    val observerHideKeyboard = Observer<Boolean> {
        if (it) hideKeyboard()
    }

    abstract fun hideKeyboard()

    abstract fun setToolbarElements()

    val observerNetworkError = Observer<ResponseStatusError> {
        when (it) {
            is ResponseStatusError.NoInternetError -> {
                if (::master.isInitialized) {
                    NoInternetSheet().apply {
                        show(master.supportFragmentManager, NoInternetSheet.TAG)
                    }
                }
            }

            is ResponseStatusError.TechnicalError -> {
                if (::master.isInitialized) {
                    ErrorSheet(getString(R.string.boom_lotto_error), getString(it.errorMessageCode), getString(
                        R.string.close)) { }.apply {
                        show(master.supportFragmentManager, ErrorSheet.TAG)
                    }
                }
            }
        }
    }

}