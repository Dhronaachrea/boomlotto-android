package com.skilrock.boomlotto.models.response


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryListResponse(

    @SerializedName("data")
    @Expose
    val data: ArrayList<Data?>?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?
) : Parcelable, AppResponse {
    @Parcelize
    data class Data(

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("countryName")
        @Expose
        val countryName: String?,

        @SerializedName("flag")
        @Expose
        val flag: String?,

        @SerializedName("isDefault")
        @Expose
        val isDefault: Boolean?,

        @SerializedName("isdCode")
        @Expose
        val isdCode: String?
    ) : Parcelable
}