package com.skilrock.boomlotto.viewmodels

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.request.EmailOtpRequest
import com.skilrock.boomlotto.models.request.EmailVerificationRequest
import com.skilrock.boomlotto.models.response.EmailOtpResponse
import com.skilrock.boomlotto.models.response.PlayerProfileResponse
import com.skilrock.boomlotto.repository.IdVerificationRepository
import com.skilrock.boomlotto.repository.PlayerEmailUpdateRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerEmailUpdateViewModel : BaseViewModel() {

    val liveDataEmailOtp            = MutableLiveData<ResponseStatus<EmailOtpResponse>>()
    val liveDataEmailVerification   = MutableLiveData<ResponseStatus<EmailOtpResponse>>()
    val liveDataPlayerProfile       = MutableLiveData<ResponseStatus<PlayerProfileResponse>>()
    val liveDataEmailError          = MutableLiveData<Unit>()
    val liveDataAmountError         = MutableLiveData<Unit>()
    val liveDataOtpError            = MutableLiveData<Unit>()
    val liveDataOtpLoader           = MutableLiveData<Boolean>()
    var isOtpClickAllowed           = true
    var email                       = MutableLiveData<String>()
    var amount                      = MutableLiveData<String>()
    var otp                         = MutableLiveData("")

    fun callEmailOtpApi() {
        if (isOtpClickAllowed && validateEmail()) {
            liveDataHideKeyboard.postValue(true)
            liveDataOtpLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                val response = PlayerEmailUpdateRepository().callEmailOtpApi(EmailOtpRequest(emailId = email.value))
                withContext(Dispatchers.Main) { onApiResponse(liveDataEmailOtp, response) }
                liveDataOtpLoader.postValue(false)
            }
        }
    }

    fun callEmailVerificationApi() {
        liveDataHideKeyboard.postValue(true)
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = PlayerEmailUpdateRepository().callEmailVerificationApi(EmailVerificationRequest(emailId = email.value, otp = otp.value))
            withContext(Dispatchers.Main) { onApiResponse(liveDataEmailVerification, response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun validateEmail() : Boolean {
        val flag =  !TextUtils.isEmpty(email.value) && android.util.Patterns.EMAIL_ADDRESS.matcher(email.value.toString()).matches()
        return if (flag)
            true
        else {
            liveDataEmailError.postValue(Unit)
            false
        }
    }

    fun callPlayerProfileApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callPlayerProfileApi()
            withContext(Dispatchers.Main) { onApiResponse(liveDataPlayerProfile, response) }
            liveDataLoader.postValue(false)
        }
    }
}