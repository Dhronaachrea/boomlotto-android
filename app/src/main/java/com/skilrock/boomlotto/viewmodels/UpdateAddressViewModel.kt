package com.skilrock.boomlotto.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.response.*
import com.skilrock.boomlotto.repository.IdVerificationRepository
import com.skilrock.boomlotto.repository.MyProfileForPlayerRepository
import com.skilrock.boomlotto.utility.DEFAULT_RESPONSE_CODE
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Response

class UpdateAddressViewModel : BaseViewModel() {

    val liveDataCity                = MutableLiveData<ResponseStatus<ArrayList<CityResponse.Data?>>>()
    val liveDataState               = MutableLiveData<ResponseStatus<ArrayList<StateResponse.Data?>>>()
    val liveDataCountry             = MutableLiveData<ResponseStatus<CountryListResponse>>()
    val liveDataIdVerification      = MutableLiveData<ResponseStatus<IdVerificationResponse>>()
    val liveDataForPlayerProfile    = MutableLiveData<ResponseStatus<ProfileInfoForPlayerResponse>>()

    fun callCountryListApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callCountryListApi()
            withContext(Dispatchers.Main) { onApiResponse(liveDataCountry, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callIdVerificationApi(addressLine1: String, addressLine2: String, country: String, state: String, city: String, town: String, zipCode: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val verificationRequestParams = verificationRequestParams(addressLine1, addressLine2, country, state, city, town, zipCode)
            val response = IdVerificationRepository().callIdVerificationApi(verificationRequestParams)
            withContext(Dispatchers.Main) { onApiResponse(liveDataIdVerification, response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun verificationRequestParams(addressLine1: String, addressLine2: String, country: String, state: String, city: String, town: String, zipCode: String): HashMap<String, RequestBody> {
        val map = HashMap<String, RequestBody>()
        map["merchantPlayerId"]         = RequestBody.create(MediaType.parse("text/plain"), PlayerInfo.getPlayerId().toString())
        map["domainName"]               = RequestBody.create(MediaType.parse("text/plain"), BuildConfig.DOMAIN_NAME)
        map["addressLine1"]             = RequestBody.create(MediaType.parse("text/plain"), addressLine1)
        map["addressLine2"]             = RequestBody.create(MediaType.parse("text/plain"), addressLine2)
        map["country"]                  = RequestBody.create(MediaType.parse("text/plain"), country)
        map["stateCode"]                = RequestBody.create(MediaType.parse("text/plain"), state)
        map["city"]                     = RequestBody.create(MediaType.parse("text/plain"), city)
        map["town"]                     = RequestBody.create(MediaType.parse("text/plain"), town)
        map["zipCode"]                  = RequestBody.create(MediaType.parse("text/plain"), zipCode)

        return map
    }

    fun callPlayerProfileApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = MyProfileForPlayerRepository().callProfileApiForPlayer()
            withContext(Dispatchers.Main) { onApiResponse(liveDataForPlayerProfile, response) }
            liveDataLoader.postValue(false)
        }
    }

    fun callCityListApi(stateCode: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callCityListApi(stateCode)
            withContext(Dispatchers.Main) { onCityListResponse(response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun onCityListResponse(response: Response<CityResponse>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val cityListResponse: CityResponse? = response.body()
                    if (cityListResponse?.errorCode == 0) {
                        if (cityListResponse.data != null && cityListResponse.data.isNotEmpty())
                            liveDataCity.postValue(ResponseStatus.Success(cityListResponse.data))
                        else
                            liveDataCity.postValue(ResponseStatus.TechnicalError(R.string.no_city_found))
                    }
                    else
                        liveDataCity.postValue(ResponseStatus.Error(cityListResponse?.errorMessage ?: "", cityListResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveDataCity.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataCity.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataCity.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }

    fun callStateListApi(countryCode: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = IdVerificationRepository().callStateListApi(countryCode)
            withContext(Dispatchers.Main) { onStateListResponse(response) }
            liveDataLoader.postValue(false)
        }
    }

    private fun onStateListResponse(response: Response<StateResponse>) {
        try {
            when {
                response.isSuccessful && response.body() != null -> {
                    val stateListResponse: StateResponse? = response.body()
                    if (stateListResponse?.errorCode == 0) {
                        if (stateListResponse.data != null && stateListResponse.data.isNotEmpty())
                            liveDataState.postValue(ResponseStatus.Success(stateListResponse.data))
                        else
                            liveDataState.postValue(ResponseStatus.TechnicalError(R.string.no_state_found))
                    }
                    else
                        liveDataState.postValue(ResponseStatus.Error(stateListResponse?.errorMessage ?: "", stateListResponse?.errorCode ?: DEFAULT_RESPONSE_CODE))
                }

                response.errorBody() != null -> liveDataState.postValue(ResponseStatus.Error(response.errorBody().toString(), DEFAULT_RESPONSE_CODE))

                else -> liveDataState.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
            }
        } catch (e: Exception) {
            liveDataState.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
        }
    }
}