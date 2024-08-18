package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MyTicketDetailResponse(

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

        @SerializedName("agentId")
        @Expose
        val agentId: Int?,

        @SerializedName("agentName")
        @Expose
        val agentName: Int?,

        @SerializedName("calimStatus")
        @Expose
        val calimStatus: Any?,

        @SerializedName("drawWinList")
        @Expose
        val drawWinList: ArrayList<DrawWin?>?,

        @SerializedName("gameCode")
        @Expose
        val gameCode: String?,

        @SerializedName("gameId")
        @Expose
        val gameId: Int?,

        @SerializedName("gameName")
        @Expose
        val gameName: String?,

        @SerializedName("gameNo")
        @Expose
        val gameNo: Int?,

        @SerializedName("noOfDraws")
        @Expose
        val noOfDraws: Int?,

        @SerializedName("noOfLines")
        @Expose
        val noOfLines: Any?,

        @SerializedName("partyId")
        @Expose
        val partyId: PartyId?,

        @SerializedName("partyType")
        @Expose
        val partyType: String?,

        @SerializedName("playType")
        @Expose
        val playType: Any?,

        @SerializedName("pwtTimeMap")
        @Expose
        val pwtTimeMap: Any?,

        @SerializedName("retailerId")
        @Expose
        val retailerId: Int?,

        @SerializedName("retailerName")
        @Expose
        val retailerName: String?,

        @SerializedName("saleAmt")
        @Expose
        val saleAmt: Double?,

        @SerializedName("saleDate")
        @Expose
        val saleDate: String?,

        @SerializedName("saleMode")
        @Expose
        val saleMode: Any?,

        @SerializedName("status")
        @Expose
        val status: Any?,

        @SerializedName("statusCheck")
        @Expose
        val statusCheck: String?,

        @SerializedName("tktNumber")
        @Expose
        val tktNumber: String?,

        @SerializedName("txnCurrency")
        @Expose
        val txnCurrency: String?
    ) {
        data class DrawWin(

            @SerializedName("appReq")
            @Expose
            val appReq: Boolean?,

            @SerializedName("claimedTime")
            @Expose
            val claimedTime: String?,

            @SerializedName("drawDateTime")
            @Expose
            val drawDateTime: String?,

            @SerializedName("drawId")
            @Expose
            val drawId: Int?,

            @SerializedName("drawStatusForticket")
            @Expose
            val drawStatusForticket: String?,

            @SerializedName("drawname")
            @Expose
            val drawname: String?,

            @SerializedName("eventId")
            @Expose
            val eventId: Int?,

            @SerializedName("highLevel")
            @Expose
            val highLevel: Boolean?,

            @SerializedName("message")
            @Expose
            val message: Any?,

            @SerializedName("messageCode")
            @Expose
            val messageCode: Any?,

            @SerializedName("panelWinList")
            @Expose
            val panelWinList: List<PanelWin?>?,

            @SerializedName("rankId")
            @Expose
            val rankId: Int?,

            @SerializedName("runTimeFlagInfo")
            @Expose
            val runTimeFlagInfo: Any?,

            @SerializedName("sideBetMatchInfo")
            @Expose
            val sideBetMatchInfo: Any?,

            @SerializedName("status")
            @Expose
            val status: String?,

            @SerializedName("tableName")
            @Expose
            val tableName: Any?,

            @SerializedName("tktStatusForDraw")
            @Expose
            val tktStatusForDraw: Any?,

            @SerializedName("valid")
            @Expose
            val valid: Boolean?,

            @SerializedName("verificationStatus")
            @Expose
            val verificationStatus: Any?,

            @SerializedName("winResult")
            @Expose
            val winResult: String?,

            @SerializedName("winningAmt")
            @Expose
            val winningAmt: String?,

            @SerializedName("winningMultiplierInfo")
            @Expose
            val winningMultiplierInfo: Any?
        ) {
            data class PanelWin(

                @SerializedName("appReq")
                @Expose
                val appReq: Boolean?,

                @SerializedName("betAmtMultiple")
                @Expose
                val betAmtMultiple: Int?,

                @SerializedName("betDispName")
                @Expose
                val betDispName: String?,

                @SerializedName("claimByPartyId")
                @Expose
                val claimByPartyId: String?,

                @SerializedName("claimByPartyName")
                @Expose
                val claimByPartyName: String?,

                @SerializedName("highLevel")
                @Expose
                val highLevel: Boolean?,

                @SerializedName("lineAmountNativeCurr")
                @Expose
                val lineAmountNativeCurr: Double?,

                @SerializedName("lineAmountTicketCurr")
                @Expose
                val lineAmountTicketCurr: Double?,

                @SerializedName("lineId")
                @Expose
                val lineId: Int?,

                @SerializedName("message")
                @Expose
                val message: Any?,

                @SerializedName("messageCode")
                @Expose
                val messageCode: Any?,

                @SerializedName("panelId")
                @Expose
                val panelId: Int?,

                @SerializedName("partyId")
                @Expose
                val partyId: Int?,

                @SerializedName("partyName")
                @Expose
                val partyName: String?,

                @SerializedName("pickType")
                @Expose
                val pickType: String?,

                @SerializedName("pickedData")
                @Expose
                val pickedData: String?,

                @SerializedName("playType")
                @Expose
                val playType: String?,

                @SerializedName("playerTxnCurrency")
                @Expose
                val playerTxnCurrency: String?,

                @SerializedName("playerWinningAmtCurrency")
                @Expose
                val playerWinningAmtCurrency: String?,

                @SerializedName("promoWinAmt")
                @Expose
                val promoWinAmt: Double?,

                @SerializedName("quickPickType")
                @Expose
                val quickPickType: String?,

                @SerializedName("rankId")
                @Expose
                val rankId: Int?,

                @SerializedName("status")
                @Expose
                val status: String?,

                @SerializedName("valid")
                @Expose
                val valid: Boolean?,

                @SerializedName("verificationStatus")
                @Expose
                val verificationStatus: Any?,

                @SerializedName("winningAmt")
                @Expose
                val winningAmt: Double?,

                @SerializedName("winningMultiplier")
                @Expose
                val winningMultiplier: Double?
            )
        }

        data class PartyId(

            @SerializedName("0")
            @Expose
            val x0: String?,

            @SerializedName("1")
            @Expose
            val x1: String?
        )
    }
}