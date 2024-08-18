package com.skilrock.boomlotto.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.IdVerificationResponse
import com.skilrock.boomlotto.repository.IdVerificationRepository
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import java.text.SimpleDateFormat

class IdVerificationViewModel : BaseViewModel() {

    val liveDataLoginCountry    = MutableLiveData<ResponseStatus<CountryListResponse>>()
    val liveDataIdVerification  = MutableLiveData<ResponseStatus<IdVerificationResponse>>()

    fun callCountryListApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callCountryListApi()
            withContext(Dispatchers.Main) { onApiResponse(liveDataLoginCountry, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callIdVerificationApi(firstName: String, lastName: String, dob: String, nationality: String, docName: String, docValue: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callIdVerificationApi(verificationRequestParams(firstName, lastName, dob, nationality, docName, docValue))
            withContext(Dispatchers.Main) { onApiResponse(liveDataIdVerification, response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun verificationRequestParams(firstName: String, lastName: String, dob: String, nationality: String, docName: String, docValue: String): HashMap<String, RequestBody> {
        val map = HashMap<String, RequestBody>()
        map["merchantPlayerId"]         = RequestBody.create(MediaType.parse("text/plain"), PlayerInfo.getPlayerId().toString())
        map["domainName"]               = RequestBody.create(MediaType.parse("text/plain"), BuildConfig.DOMAIN_NAME)
        map["firstName"]                = RequestBody.create(MediaType.parse("text/plain"), firstName)
        map["lastName"]                 = RequestBody.create(MediaType.parse("text/plain"), lastName)
        map["dob"]                      = RequestBody.create(MediaType.parse("text/plain"), getFormattedDob(dob))
        map["nationality"]              = RequestBody.create(MediaType.parse("text/plain"), nationality)
        map["documents[0].docType"]     = RequestBody.create(MediaType.parse("text/plain"), "ID_PROOF")
        map["documents[0].docName"]     = RequestBody.create(MediaType.parse("text/plain"), docName)
        map["documents[0].docValue"]    = RequestBody.create(MediaType.parse("text/plain"), docValue)
        return map
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDob(transactionDate: String) : String {
        return try {
            val parser      = SimpleDateFormat("MMM dd, yyyy")
            val formatter   = SimpleDateFormat("yyyy-MM-dd")
            parser.parse(transactionDate)?.let { formatter.format(it) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}