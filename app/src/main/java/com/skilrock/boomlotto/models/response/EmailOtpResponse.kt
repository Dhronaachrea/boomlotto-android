package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EmailOtpResponse(

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?
) : AppResponse