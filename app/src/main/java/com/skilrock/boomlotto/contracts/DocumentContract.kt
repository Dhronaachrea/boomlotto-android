package com.skilrock.boomlotto.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract

class DocumentContract : ActivityResultContract<Unit, Uri>() {

    override fun createIntent(context: Context, input: Unit?): Intent {
        //val mimeTypes = arrayOf("image/*", "application/pdf")
        val mimeTypes = arrayOf("image/*")
        return Intent(Intent.ACTION_PICK)
            //.setType("image/*|application/pdf")
            .setType("image/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        val uri: Uri? = intent?.data
        Log.i("log", "Uri: $uri")
        return uri
    }

}