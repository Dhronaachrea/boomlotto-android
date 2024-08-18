package com.skilrock.boomlotto.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract

class DocumentPdfSelect: ActivityResultContract<Unit, Uri>() {

    override fun createIntent(context: Context, input: Unit?): Intent {
        val mimeTypes = arrayOf("application/pdf")
        return Intent(Intent.ACTION_GET_CONTENT)
            .setType("application/pdf")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        val uri: Uri? = intent?.data
        Log.i("log", "Uri: $uri")
        return uri
    }
}