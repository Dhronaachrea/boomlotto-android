package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DocumentListResponse(

    @SerializedName("data")
    @Expose
    val `data`: List<Data?>?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String?
) {
    data class Data(

        @SerializedName("docList")
        @Expose
        val docList: List<Doc?>?,

        @SerializedName("docType")
        @Expose
        val docType: String?,

        @SerializedName("docTypeDisplayName")
        @Expose
        val docTypeDisplayName: String?
    ) {
        data class Doc(

            @SerializedName("docDisplayName")
            @Expose
            val docDisplayName: String?,

            @SerializedName("docName")
            @Expose
            val docName: String?
        )
    }
}