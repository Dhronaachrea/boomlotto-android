package com.skilrock.boomlotto.repository

import com.skilrock.boomlotto.models.request.PlayerProfileRequest
import com.skilrock.boomlotto.models.response.*
import com.skilrock.boomlotto.network.RetrofitNetworking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class IdVerificationRepository {

    suspend fun callCountryListApi() : Response<CountryListResponse> {
        return RetrofitNetworking.create().callCountryListApi()
    }

    suspend fun callIdVerificationApi(requestParamsMap: HashMap<String, RequestBody>) : Response<IdVerificationResponse>{
        return RetrofitNetworking.create().callIdVerificationApi(paramsMap = requestParamsMap)
    }

    suspend fun callIdVerificationWithdrawalApi(requestParamsMap: HashMap<String, RequestBody>, multipartFileList: ArrayList<MultipartBody.Part>) : Response<IdVerificationResponse>{
        return RetrofitNetworking.create().callIdVerificationWithdrawalApi(paramsMap = requestParamsMap, imageFileList = multipartFileList)
    }

    suspend fun callPlayerProfileApi() : Response<PlayerProfileResponse>{
        return RetrofitNetworking.create().callPlayerProfileApi(playerProfileRequest = PlayerProfileRequest())
    }

    suspend fun callStateListApi(countryCode: String) : Response<StateResponse>{
        return RetrofitNetworking.create().callStateListApi(countryCode = countryCode)
    }

    suspend fun callCityListApi(stateCode: String) : Response<CityResponse>{
        return RetrofitNetworking.create().callCityListApi(stateCode = stateCode)
    }

}