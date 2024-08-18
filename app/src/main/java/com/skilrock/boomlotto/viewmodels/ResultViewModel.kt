package com.skilrock.boomlotto.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.request.ResultRequest
import com.skilrock.boomlotto.models.response.ResultResponse
import com.skilrock.boomlotto.repository.ResultRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class ResultViewModel : BaseViewModel() {

    val liveDataResult  = MutableLiveData<ResponseStatus<ResultResponse>>()

    fun callResultApi(startDate: String, endDate: String) {
        //liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = ResultRepository().callResultApi(ResultRequest(fromDate = startDate, toDate = endDate))
            withContext(Dispatchers.Main) { onApiResponse(liveDataResult, response) }
            //liveDataLoader.postValue(false)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateForRequestParam(strDate: String) : String {
        val input   = SimpleDateFormat("MMM dd, yyyy")
        val output  = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            input.parse(strDate)?.let {
                return output.format(it)
            }
        } catch (e: Exception) {
            Log.e("log", "Date parsing error: ${e.message}")
        }
        return ""
    }
}