package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HeaderInfoResponse(

    @SerializedName("cashbal")
    @Expose
    val cashbal: Double?,

    @SerializedName("data")
    @Expose
    val `data`: Data?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("practiceBal")
    @Expose
    val practiceBal: Double?,

    @SerializedName("profileStatus")
    @Expose
    val profileStatus: String?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("totalBonus")
    @Expose
    val totalBonus: Double?,

    @SerializedName("totalDeposit")
    @Expose
    val totalDeposit: Double?,

    @SerializedName("totalWgr")
    @Expose
    val totalWgr: Double?,

    @SerializedName("totalWinning")
    @Expose
    val totalWinning: Double?,

    @SerializedName("totalWithdrawal")
    @Expose
    val totalWithdrawal: Double?,

    @SerializedName("unreadMsgCount")
    @Expose
    val unreadMsgCount: Int?,

    @SerializedName("withdrawableBal")
    @Expose
    val withdrawableBal: Double?,

    var apiCalledSourceTag: String = ""
) {
    data class Data(

        @SerializedName("bonusBarInfo")
        @Expose
        val bonusBarInfo: String?
    )
}