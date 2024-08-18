package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.request.BonusRequest
import com.skilrock.boomlotto.models.request.LoginRequest
import com.skilrock.boomlotto.models.request.ReferCodeRequest
import com.skilrock.boomlotto.models.response.BonusResponse
import com.skilrock.boomlotto.models.response.LoginOtpResponse
import com.skilrock.boomlotto.models.response.LoginResponse
import com.skilrock.boomlotto.models.response.ReferCodeResponse
import com.skilrock.boomlotto.repository.BonusRepository
import com.skilrock.boomlotto.repository.LoginRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginOtpViewModel : BaseViewModel() {

    val liveDataLogin           = MutableLiveData<ResponseStatus<LoginResponse>>()
    val liveDataLoginOpt        = MutableLiveData<ResponseStatus<LoginOtpResponse>>()
    val liveDataReferCode       = MutableLiveData<ResponseStatus<ReferCodeResponse>>()
    val liveDataReferralLoader  = MutableLiveData<Boolean>()
    val liveDataBonus           = MutableLiveData<ResponseStatus<BonusResponse>>()

    fun callBonusApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = BonusRepository().callBonusApi(BonusRequest())
            withContext(Dispatchers.Main) { onApiResponse(liveDataBonus, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callLoginApi(loginRequest: LoginRequest) {
        liveDataHideKeyboard.postValue(true)
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = LoginRepository().callLoginApi(loginRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDataLogin, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callOtpApi(mobileNumber: String) {
        liveDataHideKeyboard.postValue(true)
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = LoginRepository().callOtpApi(mobileNumber)
            withContext(Dispatchers.Main) { onApiResponse(liveDataLoginOpt, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callReferCodeApi(referCode: String) {
        liveDataHideKeyboard.postValue(true)
        liveDataReferralLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = LoginRepository().callReferCodeApi(ReferCodeRequest(referCode = referCode))
            withContext(Dispatchers.Main) { onApiResponse(liveDataReferCode, response) }
            liveDataReferralLoader.postValue(false)
        }
    }
}