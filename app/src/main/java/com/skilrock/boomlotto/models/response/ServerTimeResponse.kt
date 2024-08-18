package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ServerTimeResponse(

    @SerializedName("data")
    @Expose
    val `data`: Data?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("message")
    @Expose
    override val errorMessage: String?
) : AppResponse {
    data class Data(

        @SerializedName("date")
        @Expose
        val date: String?,

        @SerializedName("timezone")
        @Expose
        val timezone: String?,

        @SerializedName("timezone_type")
        @Expose
        val timezoneType: Int?
    )
}