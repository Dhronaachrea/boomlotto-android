package com.skilrock.boomlotto.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.phone.SmsRetriever

class SmsResultContract : ActivityResultContract<Intent, String>() {

    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return if (resultCode == Activity.RESULT_OK && intent != null)
            intent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
        else
            null
    }

}