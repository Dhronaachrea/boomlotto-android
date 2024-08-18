package com.skilrock.boomlotto.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver(private val onSuccess:(Intent?)->Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action === SmsRetriever.SMS_RETRIEVED_ACTION) {
            intent.extras?.let { extras ->
                val smsRetrieverStatus: Status? = extras[SmsRetriever.EXTRA_STATUS] as Status?
                when (smsRetrieverStatus?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val messageIntent: Intent? = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                        onSuccess(messageIntent)
                    }

                    CommonStatusCodes.TIMEOUT -> {}
                }
            }
        }
    }

}