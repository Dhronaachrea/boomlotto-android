package com.skilrock.boomlotto.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.ui.activity.CountrySelectionActivity

class CountrySelectionContract : ActivityResultContract<ArrayList<CountryListResponse.Data?>, CountryListResponse.Data?>() {

    override fun createIntent(context: Context, input: ArrayList<CountryListResponse.Data?>): Intent {
        val intentCountry = Intent(context, CountrySelectionActivity::class.java)
        intentCountry.putParcelableArrayListExtra("countryList", input)
        return intentCountry
    }

    override fun parseResult(resultCode: Int, intent: Intent?): CountryListResponse.Data? {
        val selectedCountry = intent?.getParcelableExtra<CountryListResponse.Data>("selectedCountry")

        return if (resultCode == Activity.RESULT_OK && selectedCountry != null)
            selectedCountry
        else
            null
    }

}