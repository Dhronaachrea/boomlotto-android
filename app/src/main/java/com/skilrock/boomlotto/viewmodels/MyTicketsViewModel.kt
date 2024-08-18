package com.skilrock.boomlotto.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.models.request.MyTicketDetailRequest
import com.skilrock.boomlotto.models.request.ServerTimeRequest
import com.skilrock.boomlotto.models.response.InstantGameTicketListResponse
import com.skilrock.boomlotto.models.response.MyTicketDetailResponse
import com.skilrock.boomlotto.models.response.MyTicketsListResponse
import com.skilrock.boomlotto.models.response.ServerTimeResponse
import com.skilrock.boomlotto.repository.MyTicketRepository
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MyTicketsViewModel : BaseViewModel() {

    val liveDataTicketDetail            = MutableLiveData<ResponseStatus<MyTicketDetailResponse>>()
    val liveDataTicketList              = MutableLiveData<ResponseStatus<MyTicketsListResponse>>()
    val liveDataServerTime              = MutableLiveData<ResponseStatus<ServerTimeResponse>>()
    val liveDataInstantGameTicketList   = MutableLiveData<ResponseStatus<InstantGameTicketListResponse>>()

    fun callServerTimeApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = MyTicketRepository().callServerTimeApi(ServerTimeRequest())
            withContext(Dispatchers.Main) { onApiResponse(liveDataServerTime, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callTicketDetailApi(myTicketDetailRequest: MyTicketDetailRequest) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = MyTicketRepository().callTicketDetailApi(myTicketDetailRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDataTicketDetail, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callTicketListApi() {
        //liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = MyTicketRepository().callTicketListApi()
            withContext(Dispatchers.Main) { onApiResponse(liveDataTicketList, response) }
            //liveDataLoader.postValue(false)
        }
    }

    fun callInstantGameTicketListApi() {
        //liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = MyTicketRepository().callInstantGameTicketList()
            withContext(Dispatchers.Main) { onApiResponse(liveDataInstantGameTicketList, response) }
            //liveDataLoader.postValue(false)
        }
    }

    fun getTimeDifference(futureDateStr: String, currentDateStr: String): Long {
        var diff: Long = 0
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val futureDate: Date? = dateFormat.parse(futureDateStr)
            val currentDate: Date? = dateFormat.parse(currentDateStr)
            futureDate?.let { fDate ->
                currentDate?.let { cDate ->
                    diff = fDate.time - cDate.time
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("log", e.message ?: "ERROR")
        }
        return diff
    }

}