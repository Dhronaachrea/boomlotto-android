package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StateResponse(

    @SerializedName("data")
    @Expose
    val `data`: ArrayList<Data?>?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String?
) {
    data class Data(

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("stateCode")
        @Expose
        val stateCode: String?,

        @SerializedName("stateName")
        @Expose
        val stateName: String?
    )
}