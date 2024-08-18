package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BannerResponse(

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

        @SerializedName("DRAW")
        @Expose
        val dRAW: List<Any?>?,

        @SerializedName("HOME")
        @Expose
        val hOME: ArrayList<HOME?>?,

        @SerializedName("HOWTO")
        @Expose
        val hOWTO: List<Any?>?,

        @SerializedName("IGE")
        @Expose
        val iGE: List<Any?>?,

        @SerializedName("PLAYNOW")
        @Expose
        val pLAYNOW: List<Any?>?,

        @SerializedName("RESULT")
        @Expose
        val rESULT: List<Any?>?
    ) {
        data class HOME(

            @SerializedName("gameCode")
            @Expose
            val gameCode: String?,

            @SerializedName("imageItem")
            @Expose
            val imageItem: String?,

            @SerializedName("title")
            @Expose
            val title: String?
        )
    }
}