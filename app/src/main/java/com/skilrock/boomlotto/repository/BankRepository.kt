package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.AddNewAccountRequest
import com.skilrock.boomlotto.models.request.WithdrawalRequest
import com.skilrock.boomlotto.models.response.AddNewAccountResponse
import com.skilrock.boomlotto.models.response.BankDocUploadResponse
import com.skilrock.boomlotto.models.response.WithdrawResponse
import com.skilrock.boomlotto.network.RetrofitNetworking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class BankRepository {

    suspend fun callAddNewAccountApi(addNewAccountRequest: AddNewAccountRequest) : Response<AddNewAccountResponse> {
        return RetrofitNetworking.create().callAddNewAccountApi(addNewAccountRequest = addNewAccountRequest)
    }

    suspend fun callWithdrawRequestApi(withdrawalRequest: WithdrawalRequest) : Response<WithdrawResponse> {
        return RetrofitNetworking.create().callWithdrawRequestApi(withdrawalRequest=withdrawalRequest)
    }

    suspend fun callPlayerDocUploadApi(requestParamsMap: HashMap<String, RequestBody>, multipartFileList: ArrayList<MultipartBody.Part>) : Response<BankDocUploadResponse>{
        return RetrofitNetworking.create().callPlayerDocUploadApi(paramsMap = requestParamsMap, imageFileList = multipartFileList)
    }

}