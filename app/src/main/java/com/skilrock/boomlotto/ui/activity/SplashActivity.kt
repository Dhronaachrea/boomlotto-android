package com.skilrock.boomlotto.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.ActivitySplashBinding
import com.skilrock.boomlotto.dialogs.AppUpdateSheet
import com.skilrock.boomlotto.models.request.BannerRequest
import com.skilrock.boomlotto.models.response.BannerResponse
import com.skilrock.boomlotto.models.response.InstantGameListResponse
import com.skilrock.boomlotto.models.response.LoginResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        setDataBindingAndViewModel()
        setVersion()
        checkAppVersionTest()
    }

    override fun setDataBindingAndViewModel() {
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        viewModel   = ViewModelProvider(this)[SplashViewModel::class.java]

        binding.lifecycleOwner  = this
        binding.viewModel       = viewModel

        viewModel.liveDataBanner.observe(this, observerBanner)
        viewModel.liveDataInstantGameList.observe(this, observerInstantGameList)
        viewModel.liveDataTestVersion.observe(this, observerVersionTest)
    }

    private fun setVersion() {
        binding.tvVersion.text = (getString(R.string.version) + " " + BuildConfig.VERSION_NAME)
    }

    private fun checkAppVersionTest() {
        if (BuildConfig.BUILD_TYPE == BUILD_TYPE_UAT) {
            viewModel.callTestVersionApi()
        } else {
            callBannerApi()
        }
    }

    private fun callBannerApi() {
        viewModel.callBannerApi(BannerRequest())
    }

    private fun proceedToHome() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty())
            PlayerInfo.destroy()
        else
            PlayerInfo.setLoginData(Gson().fromJson(SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_DATA), LoginResponse::class.java))

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private val observerVersionTest = Observer<Pair<Boolean, String>> { pair ->
        if (pair.first) {
            AppUpdateSheet(pair.second) { this@SplashActivity.finish() }.apply {
                show(supportFragmentManager, AppUpdateSheet.TAG)
            }
        } else {
            callBannerApi()
        }
    }

    private val observerBanner = Observer<ResponseStatus<BannerResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                val bannerList = it.response.data?.hOME
                if (bannerList == null || bannerList.isEmpty())
                    Log.e("log", "Banner Response: List is empty.")
                else
                    saveBannerData(bannerList)
            }

            is ResponseStatus.Error -> {
                val errorMessage = getResponseMessage(this, CMS, it.errorCode)
                showToast(errorMessage)
            }

            is ResponseStatus.TechnicalError -> showToast(getString(it.errorMessageCode))
        }
        viewModel.callGameListApi()
    }

    private fun saveBannerData(bannerList: ArrayList<BannerResponse.Data.HOME?>) {
        val bannerSet = LinkedHashSet<String>()
        bannerList.forEach { banner ->
            banner?.imageItem?.let { url -> bannerSet.add(url) }
        }

        if (bannerSet.isNotEmpty())
            SharedPrefUtils.setBanner(this, bannerSet)
    }

    override fun hideKeyboard() {

    }

    private val observerInstantGameList = Observer<ResponseStatus<InstantGameListResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                Log.w("log", "Response::: ${it.response}")
                val response: InstantGameListResponse = it.response
                SharedPrefUtils.setInstantGameListResponse(this, Gson().toJson(response))
            }

            is ResponseStatus.Error -> {
                Log.e("log", "Error: ${getResponseMessage(this, CMS, it.errorCode)}")
                Log.e("log", "Error: ${it.errorMessage}")
            }

            is ResponseStatus.TechnicalError -> Log.e("log", "Error: ${getString(it.errorMessageCode)}")
        }

        lifecycleScope.launch {
            delay(1000)
            proceedToHome()
        }
    }

}