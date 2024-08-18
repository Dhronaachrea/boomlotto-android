package com.skilrock.boomlotto.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.CountryAdapter
import com.skilrock.boomlotto.databinding.ActivityCountrySelectionBinding
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.utility.afterTextChanged
import com.skilrock.boomlotto.utility.hideKeyboard
import com.skilrock.boomlotto.utility.showToast

class CountrySelectionActivity : BaseActivity() {

    private lateinit var binding: ActivityCountrySelectionBinding
    private lateinit var countryAdapter: CountryAdapter
    private lateinit var countryList: ArrayList<CountryListResponse.Data?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowStyling()
        setDataBindingAndViewModel()
        receiveDataFromPreviousActivity()
        setRecyclerView()
        performSearchOperation()
        setClickListeners()
    }

    override fun setDataBindingAndViewModel() {
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_country_selection)

        binding.lifecycleOwner  = this
    }

    private fun receiveDataFromPreviousActivity() {
        intent?.run {
            try {
                countryList = extras?.getParcelableArrayList<CountryListResponse.Data?>("countryList") as ArrayList<CountryListResponse.Data?>
            } catch (e: Exception) {
                showToast(getString(R.string.some_technical_issue))
                finish()
            }
        }
    }

    private fun setRecyclerView() {
        countryAdapter = CountryAdapter(::onCountrySelected)
        binding.rvCountry.apply {
            layoutManager = LinearLayoutManager(this@CountrySelectionActivity)
            setHasFixedSize(true)
            adapter = countryAdapter
        }
        countryAdapter.setCountryList(countryList)
    }

    private fun onCountrySelected(country: CountryListResponse.Data?) {
        setResult(RESULT_OK, Intent().putExtra("selectedCountry", country))
        finish()
        overridePendingTransition(R.anim.activity_fade_in_fast, R.anim.activity_fade_out_fast)
    }

    private fun performSearchOperation() {
        binding.etSearch.afterTextChanged {
            countryAdapter.searchFilter(it)
        }
    }

    override fun hideKeyboard() {
        binding.etSearch.hideKeyboard()
    }

    private fun setClickListeners() {
        binding.llBackIcon.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.activity_fade_in_fast, R.anim.activity_fade_out_fast)
    }
}