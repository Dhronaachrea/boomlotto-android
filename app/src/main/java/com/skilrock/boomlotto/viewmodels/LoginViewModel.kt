package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.models.response.LoginOtpResponse
import com.skilrock.boomlotto.repository.LoginRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : BaseViewModel() {

    var mobileNumber            = ""
    var code                    = MutableLiveData<String>()
    val liveDataLoginOpt        = MutableLiveData<ResponseStatus<LoginOtpResponse>>()
    val liveDataLoginCountry    = MutableLiveData<ResponseStatus<CountryListResponse>>()

    fun callCountryListApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = LoginRepository().callCountryListApi()
            withContext(Dispatchers.Main) { onApiResponse(liveDataLoginCountry, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callOtpApi() {
        liveDataHideKeyboard.postValue(true)
        if (validateMobileNumber()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                val response = LoginRepository().callOtpApi(getMobileNumberWithCountryCode())
                withContext(Dispatchers.Main) { onApiResponse(liveDataLoginOpt, response) }
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun validateMobileNumber() : Boolean {
        if (mobileNumber.trim().isBlank()) {
            return false
        }

        return true
    }

    private fun getMobileNumberWithCountryCode() : String {
        var countryCode: String = code.value.toString()
        countryCode = countryCode.replace("+", "")
        return countryCode + mobileNumber
    }
}