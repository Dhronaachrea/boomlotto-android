package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BonusResponse(

    @SerializedName("data")
    @Expose
    val `data`: List<Data?>?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?
) : AppResponse {
    data class Data(

        @SerializedName("activityId")
        @Expose
        val activityId: Int?,

        @SerializedName("aliasId")
        @Expose
        val aliasId: Int?,

        @SerializedName("aliasName")
        @Expose
        val aliasName: String?,

        @SerializedName("bonusId")
        @Expose
        val bonusId: Int?,

        @SerializedName("confirmBonusId")
        @Expose
        val confirmBonusId: Int?,

        @SerializedName("currencyId")
        @Expose
        val currencyId: Any?,

        @SerializedName("domainId")
        @Expose
        val domainId: Int?,

        @SerializedName("id")
        @Expose
        val id: Int?,

        @SerializedName("inBonusDetail")
        @Expose
        val inBonusDetail: Any?,

        @SerializedName("isCancel")
        @Expose
        val isCancel: Any?,

        @SerializedName("merchantId")
        @Expose
        val merchantId: Int?,

        @SerializedName("merchantUserId")
        @Expose
        val merchantUserId: Int?,

        @SerializedName("receivedBonus")
        @Expose
        val receivedBonus: Double?,

        @SerializedName("receivedBonusNative")
        @Expose
        val receivedBonusNative: Any?,

        @SerializedName("receivedCash")
        @Expose
        val receivedCash: Int?,

        @SerializedName("receivedCashNative")
        @Expose
        val receivedCashNative: Any?,

        @SerializedName("retry")
        @Expose
        val retry: Int?,

        @SerializedName("status")
        @Expose
        val status: String?,

        @SerializedName("transactionDate")
        @Expose
        val transactionDate: Long?,

        @SerializedName("transactionId")
        @Expose
        val transactionId: Int?,

        @SerializedName("userId")
        @Expose
        val userId: Int?
    )
}