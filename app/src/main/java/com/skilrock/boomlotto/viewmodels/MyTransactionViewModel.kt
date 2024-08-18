package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.request.MyTransactionsRequest
import com.skilrock.boomlotto.models.response.MyTransactionsResponse
import com.skilrock.boomlotto.repository.MyTransactionRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyTransactionViewModel: BaseViewModel() {

    val liveDataMyTransaction = MutableLiveData<ResponseStatus<MyTransactionsResponse>>()

    fun callMyTransactionApi(myTransactionsRequest: MyTransactionsRequest) {

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = MyTransactionRepository().callTransactionApi(myTransactionsRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDataMyTransaction, response) }
        }
    }
}