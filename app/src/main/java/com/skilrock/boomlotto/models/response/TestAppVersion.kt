package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TestAppVersion(

    @SerializedName("android")
    @Expose
    val android: Android?,

    @SerializedName("ios")
    @Expose
    val ios: Ios?
) {
    data class Android(

        @SerializedName("appVersion")
        @Expose
        val appVersion: String?,

        @SerializedName("message")
        @Expose
        val message: String?
    )

    data class Ios(

        @SerializedName("appVersion")
        @Expose
        val appVersion: String?,

        @SerializedName("message")
        @Expose
        val message: String?
    )
}