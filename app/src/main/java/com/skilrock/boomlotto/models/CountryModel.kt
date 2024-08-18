package com.skilrock.boomlotto.models


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryModel(

    @SerializedName("code")
    @Expose
    val code: String?,

    @SerializedName("dial_code")
    @Expose
    val dialCode: String?,

    @SerializedName("flag")
    @Expose
    val flag: String?,

    @SerializedName("name")
    @Expose
    val name: String?
) : Parcelable