package com.skilrock.boomlotto.ui.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.ActivityLoginBinding
import com.skilrock.boomlotto.utility.hideKeyboard
import com.skilrock.boomlotto.viewmodels.LoginViewModel

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    lateinit var mNavController: NavController
    private var isBackAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowStyling()
        setDataBindingAndViewModel()
        initializeNavigationComponents()
        setClickListeners()
    }

    override fun setDataBindingAndViewModel() {
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel   = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.lifecycleOwner  = this
        binding.viewModel       = viewModel

        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataLoader.observe(this, observerLoader)
    }

    override fun hideKeyboard() {

    }

    private fun initializeNavigationComponents() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_login) as NavHostFragment
        mNavController      = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return mNavController.navigateUp()
    }

    private fun setClickListeners() {
        binding.llBackIcon.setOnClickListener {
            binding.llBackIcon.hideKeyboard()
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (isBackAllowed) {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

}