package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.request.WithdrawalPendingRequest
import com.skilrock.boomlotto.models.response.WithdrawalPendingResponse
import com.skilrock.boomlotto.repository.WithdrawalPendingRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WithdrawalPendingTransactionsViewModel : BaseViewModel() {

    val liveDataPendingResponse = MutableLiveData<ResponseStatus<WithdrawalPendingResponse>>()

    fun callWithdrawalPendingApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = WithdrawalPendingRepository().callWithdrawalPendingApi(WithdrawalPendingRequest())
            withContext(Dispatchers.Main) { onApiResponse(liveDataPendingResponse, response) }
            liveDataLoader.postValue(false)
        }
    }
}