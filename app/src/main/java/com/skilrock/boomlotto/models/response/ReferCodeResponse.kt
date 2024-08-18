package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReferCodeResponse(

    @SerializedName("data")
    @Expose
    val `data`: Data?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?
) : AppResponse {
    data class Data(

        @SerializedName("referPlayerBonus")
        @Expose
        val referPlayerBonus: Double?,

        @SerializedName("referedFriendRegistration")
        @Expose
        val referedFriendRegistration: Double?
    )
}