package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.response.ProfileInfoForPlayerResponse
import com.skilrock.boomlotto.repository.MyProfileForPlayerRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyProfileViewModel : BaseViewModel() {

    val liveDataForPlayerProfile = MutableLiveData<ResponseStatus<ProfileInfoForPlayerResponse>>()

    fun callPlayerProfileApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = MyProfileForPlayerRepository().callProfileApiForPlayer()
            withContext(Dispatchers.Main) { onApiResponse(liveDataForPlayerProfile, response) }
            liveDataLoader.postValue(false)
        }
    }
}