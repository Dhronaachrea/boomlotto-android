package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BoomLottoBuyTicketResponse(

    @SerializedName("responseCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("responseData")
    @Expose
    val responseData: ResponseData?,

    @SerializedName("responseMessage")
    @Expose
    override val errorMessage: String?
) : AppResponse {
    data class ResponseData(

        @SerializedName("availableBal")
        @Expose
        val availableBal: String?,

        @SerializedName("channel")
        @Expose
        val channel: String?,

        @SerializedName("currencyCode")
        @Expose
        val currencyCode: String?,

        @SerializedName("domainCode")
        @Expose
        val domainCode: String?,

        @SerializedName("drawData")
        @Expose
        val drawData: List<DrawData?>?,

        @SerializedName("gameCode")
        @Expose
        val gameCode: String?,

        @SerializedName("gameId")
        @Expose
        val gameId: Int?,

        @SerializedName("gameName")
        @Expose
        val gameName: String?,

        @SerializedName("merchantCode")
        @Expose
        val merchantCode: String?,

        @SerializedName("nativeCurrencyCode")
        @Expose
        val nativeCurrencyCode: String?,

        @SerializedName("noOfDraws")
        @Expose
        val noOfDraws: Int?,

        @SerializedName("panelData")
        @Expose
        val panelData: List<PanelData?>?,

        @SerializedName("partyType")
        @Expose
        val partyType: String?,

        @SerializedName("playerPurchaseAmount")
        @Expose
        val playerPurchaseAmount: Double?,

        @SerializedName("productInfo")
        @Expose
        val productInfo: ProductInfo?,

        @SerializedName("purchaseTime")
        @Expose
        val purchaseTime: String?,

        @SerializedName("ticketExpiry")
        @Expose
        val ticketExpiry: String?,

        @SerializedName("ticketNumber")
        @Expose
        val ticketNumber: String?,

        @SerializedName("totalPurchaseAmount")
        @Expose
        val totalPurchaseAmount: Double?,

        @SerializedName("validationCode")
        @Expose
        val validationCode: String?
    ) {
        data class DrawData(

            @SerializedName("drawDate")
            @Expose
            val drawDate: String?,

            @SerializedName("drawName")
            @Expose
            val drawName: String?,

            @SerializedName("drawTime")
            @Expose
            val drawTime: String?
        )

        data class PanelData(

            @SerializedName("betAmountMultiple")
            @Expose
            val betAmountMultiple: Int?,

            @SerializedName("betDisplayName")
            @Expose
            val betDisplayName: String?,

            @SerializedName("betType")
            @Expose
            val betType: String?,

            @SerializedName("numberOfLines")
            @Expose
            val numberOfLines: Int?,

            @SerializedName("panelPrice")
            @Expose
            val panelPrice: Double?,

            @SerializedName("pickConfig")
            @Expose
            val pickConfig: String?,

            @SerializedName("pickDisplayName")
            @Expose
            val pickDisplayName: String?,

            @SerializedName("pickType")
            @Expose
            val pickType: String?,

            @SerializedName("pickedValues")
            @Expose
            val pickedValues: String?,

            @SerializedName("playerPanelPrice")
            @Expose
            val playerPanelPrice: Double?,

            @SerializedName("qpPreGenerated")
            @Expose
            val qpPreGenerated: Boolean?,

            @SerializedName("quickPick")
            @Expose
            val quickPick: Boolean?,

            @SerializedName("unitCost")
            @Expose
            val unitCost: Double?,

            @SerializedName("winningMultiplier")
            @Expose
            val winningMultiplier: Any?
        )

        data class ProductInfo(

            @SerializedName("donation")
            @Expose
            val donation: List<Donation?>?
        ) {
            data class Donation(

                @SerializedName("image")
                @Expose
                val image: String?,

                @SerializedName("title")
                @Expose
                val title: String?
            )
        }
    }
}