package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.request.PlayerInboxDeleteRequest
import com.skilrock.boomlotto.models.request.PlayerInboxReadRequest
import com.skilrock.boomlotto.models.request.PlayerInboxRequest
import com.skilrock.boomlotto.models.response.PlayerInboxReadResponse
import com.skilrock.boomlotto.models.response.PlayerInboxResponse
import com.skilrock.boomlotto.repository.PlayerInboxRepository
import com.skilrock.boomlotto.utility.DEFAULT_RESPONSE_CODE
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class PlayerInboxViewModel: BaseViewModel() {

    val liveDataPlayerInbox     = MutableLiveData<ResponseStatus<PlayerInboxResponse>>()
    val liveDataDeleteMessage   = MutableLiveData<ResponseStatus<PlayerInboxResponse>>()
    val liveDataReadMessage     = MutableLiveData<ResponseStatus<PlayerInboxReadResponse>>()

    fun callPlayerInboxApi(playerInboxRequest: PlayerInboxRequest) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = PlayerInboxRepository().callPlayerInboxApi(playerInboxRequest)
            withContext(Dispatchers.Main) { onApiResponse(liveDataPlayerInbox, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callDeleteMessageApi(inboxIdList: ArrayList<Int>) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = PlayerInboxRepository().callInboxDeleteApi(PlayerInboxDeleteRequest(inboxIdList = inboxIdList))
            withContext(Dispatchers.Main) { onApiResponse(liveDataDeleteMessage, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callReadMessageApi(inboxId: Int, position: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = PlayerInboxRepository().callInboxReadApi(PlayerInboxReadRequest(inboxId = inboxId))
            withContext(Dispatchers.Main) { onReadMessageResponse(response, inboxId, position) }
        }
    }

    private fun onReadMessageResponse(response: Response<PlayerInboxReadResponse>, inboxId: Int, position: Int) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val readResponse = response.body()
                    readResponse?.let {
                        it.inboxId  = inboxId
                        it.position = position
                        if (it.errorCode == 0) {
                            liveDataReadMessage.postValue(ResponseStatus.Success(it))
                        } else
                            liveDataReadMessage.postValue(ResponseStatus.Error(it.errorMessage ?: "", it.errorCode ?: DEFAULT_RESPONSE_CODE))
                    }
                }
                response.errorBody() != null -> {
                    liveDataReadMessage.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))
                }
                else -> {
                    liveDataReadMessage.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                }
            }
        } catch (e: Exception) {
            liveDataReadMessage.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

}