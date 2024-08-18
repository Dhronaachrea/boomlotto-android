package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AppAnalyticsResponse(

    @SerializedName("data")
    @Expose
    val `data`: Data?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?
) : AppResponse {
    data class Data(

        @SerializedName("analyticsData")
        @Expose
        val analyticsData: Any?,

        @SerializedName("createdAt")
        @Expose
        val createdAt: Long?,

        @SerializedName("device")
        @Expose
        val device: String?,

        @SerializedName("id")
        @Expose
        val id: Id?
    ) {
        data class Id(

            @SerializedName("counter")
            @Expose
            val counter: Int?,

            @SerializedName("date")
            @Expose
            val date: Long?,

            @SerializedName("machineIdentifier")
            @Expose
            val machineIdentifier: Int?,

            @SerializedName("processIdentifier")
            @Expose
            val processIdentifier: Int?,

            @SerializedName("time")
            @Expose
            val time: Long?,

            @SerializedName("timeSecond")
            @Expose
            val timeSecond: Int?,

            @SerializedName("timestamp")
            @Expose
            val timestamp: Int?
        )
    }
}