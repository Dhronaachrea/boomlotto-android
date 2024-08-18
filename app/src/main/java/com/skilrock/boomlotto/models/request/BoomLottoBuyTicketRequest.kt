package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.utility.PlayerInfo

data class BoomLottoBuyTicketRequest(

    @SerializedName("currencyCode")
    @Expose
    val currencyCode: String? = BuildConfig.CURRENCY_CODE,

    @SerializedName("gameCode")
    @Expose
    val gameCode: String?,

    @SerializedName("gameId")
    @Expose
    val gameId: String?,

    @SerializedName("isAdvancePlay")
    @Expose
    val isAdvancePlay: Boolean? = false,

    @SerializedName("isUpdatedPayoutConfirmed")
    @Expose
    val isUpdatedPayoutConfirmed: Boolean? = false,

    @SerializedName("lastTicketNumber")
    @Expose
    val lastTicketNumber: String? = "0",

    @SerializedName("merchantCode")
    @Expose
    val merchantCode: String? = "Weaver",

    @SerializedName("merchantData")
    @Expose
    val merchantData: MerchantData?,

    @SerializedName("noOfDraws")
    @Expose
    val noOfDraws: Int?,

    @SerializedName("panelData")
    @Expose
    val panelData: ArrayList<PanelData>?,

    @SerializedName("purchaseDeviceId")
    @Expose
    val purchaseDeviceId: String? = "1",

    @SerializedName("purchaseDeviceType")
    @Expose
    val purchaseDeviceType: String? = "ANDROID_TERMINAL",

    @SerializedName("drawData")
    @Expose
    val drawData: ArrayList<DrawData?>? = arrayListOf(DrawData("1"))
) {
    data class DrawData(

        @SerializedName("drawId")
        @Expose
        val drawId: String?
    )

    data class MerchantData(

        @SerializedName("aliasName")
        @Expose
        val aliasName: String? = BuildConfig.DOMAIN_NAME,

        @SerializedName("deviceCheck")
        @Expose
        val deviceCheck: Boolean? = false,

        @SerializedName("macAddress")
        @Expose
        val macAddress: String? = System.getProperty("http.agent"),

        @SerializedName("sessionId")
        @Expose
        val sessionId: String? = PlayerInfo.getPlayerToken(),

        @SerializedName("userId")
        @Expose
        val userId: String? = PlayerInfo.getPlayerId().toString()
    )

    data class PanelData(

        @SerializedName("betAmountMultiple")
        @Expose
        val betAmountMultiple: Int? = 1,

        @SerializedName("betType")
        @Expose
        val betType: String?,

        @SerializedName("pickConfig")
        @Expose
        val pickConfig: String?,

        @SerializedName("pickType")
        @Expose
        val pickType: String?,

        @SerializedName("pickedValues")
        @Expose
        val pickedValues: String?,

        @SerializedName("qpPreGenerated")
        @Expose
        val qpPreGenerated: Boolean? = false,

        @SerializedName("quickPick")
        @Expose
        val quickPick: Boolean?,

        @SerializedName("totalNumbers")
        @Expose
        val totalNumbers: Int?
    )
}