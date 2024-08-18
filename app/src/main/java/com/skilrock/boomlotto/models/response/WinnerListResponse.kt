package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WinnerListResponse(

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

        @SerializedName("DGE")
        @Expose
        val dGE: ArrayList<DGE?>?,

        @SerializedName("IGE")
        @Expose
        val iGE: ArrayList<IGE?>?
    ) {
        data class DGE(

            @SerializedName("amount")
            @Expose
            val amount: String?,

            @SerializedName("content")
            @Expose
            val content: String?,

            @SerializedName("datetime")
            @Expose
            val datetime: String?,

            @SerializedName("txnDate")
            @Expose
            val txnDate: String?,

            @SerializedName("game_name")
            @Expose
            val gameName: String?,

            @SerializedName("id")
            @Expose
            val id: String?,

            @SerializedName("player")
            @Expose
            val player: String?,

            @SerializedName("ref_id")
            @Expose
            val refId: String?,

            @SerializedName("serviceCode")
            @Expose
            val serviceCode: String?
        )

        data class IGE(

            @SerializedName("amount")
            @Expose
            val amount: String?,

            @SerializedName("content")
            @Expose
            val content: String?,

            @SerializedName("datetime")
            @Expose
            val datetime: String?,

            @SerializedName("txnDate")
            @Expose
            val txnDate: String?,

            @SerializedName("game_name")
            @Expose
            val gameName: String?,

            @SerializedName("id")
            @Expose
            val id: String?,

            @SerializedName("player")
            @Expose
            val player: String?,

            @SerializedName("ref_id")
            @Expose
            val refId: String?,

            @SerializedName("serviceCode")
            @Expose
            val serviceCode: String?
        )
    }
}