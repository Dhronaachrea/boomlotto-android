package com.skilrock.boomlotto.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.request.EmailOtpRequest
import com.skilrock.boomlotto.models.request.EmailVerificationRequest
import com.skilrock.boomlotto.models.response.EmailOtpResponse
import com.skilrock.boomlotto.models.response.PlayerProfileResponse
import com.skilrock.boomlotto.repository.IdVerificationRepository
import com.skilrock.boomlotto.repository.WithdrawalRepository
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WithdrawEmailVerificationViewModel : BaseViewModel() {

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
                val response = WithdrawalRepository().callEmailOtpApi(EmailOtpRequest(emailId = email.value))
                withContext(Dispatchers.Main) { onApiResponse(liveDataEmailOtp, response) }
                liveDataOtpLoader.postValue(false)
            }
        }
    }

    fun callEmailVerificationApi() {
        if (validateInputs()) {
            liveDataHideKeyboard.postValue(true)
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                val response = WithdrawalRepository().callEmailVerificationApi(EmailVerificationRequest(emailId = email.value, otp = otp.value))
                withContext(Dispatchers.Main) { onApiResponse(liveDataEmailVerification, response) }
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun validateEmail() : Boolean {
        val flag =  !TextUtils.isEmpty(email.value) && android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
        return if (flag)
            true
        else {
            liveDataEmailError.postValue(Unit)
            false
        }
    }

    fun validateInputs() : Boolean {
        try {
            if (amount.value.toString().isBlank() || amount.value.toString().trim().toDouble() <= 0) {
                liveDataAmountError.postValue(Unit)
                return false
            }
        } catch (e: Exception) {
            liveDataAmountError.postValue(Unit)
            return false
        }

        if (PlayerInfo.getPlayerEmailId().isNotBlank()) {
            return true
        } else {
            if (isOtpClickAllowed) {
                liveDataEmailError.postValue(Unit)
                return false
            }

            Log.e("log", "Length: ${otp.value.toString().trim().length}")
            if (otp.value.toString().trim().length != 4) {
                liveDataOtpError.postValue(Unit)
                return false
            }
        }

        return true
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