package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MyTicketsListResponse(

    @SerializedName("responseCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("responseData")
    @Expose
    val responseData: ArrayList<ResponseData?>?,

    @SerializedName("responseMessage")
    @Expose
    override val errorMessage: String?
) : AppResponse {
    data class ResponseData(

        @SerializedName("gameCode")
        @Expose
        val gameCode: String?,

        @SerializedName("gameName")
        @Expose
        val gameName: String?,

        @SerializedName("playerId")
        @Expose
        val playerId: String?,

        @SerializedName("ticketList")
        @Expose
        val ticketList: ArrayList<Ticket?>?
    ) {
        data class Ticket(

            @SerializedName("drawDetails")
            @Expose
            val drawDetails: DrawDetails?,

            @SerializedName("ticketDetails")
            @Expose
            val ticketDetails: TicketDetails?,

            @SerializedName("ticketNumber")
            @Expose
            val ticketNumber: String?,

            @SerializedName("transactionDate")
            @Expose
            val transactionDate: String?,

            var gameDisplayName: String = ""
        ) {
            data class DrawDetails(

                @SerializedName("drawDateTime")
                @Expose
                val drawDateTime: String?,

                @SerializedName("drawId")
                @Expose
                val drawId: Int?,

                @SerializedName("drawName")
                @Expose
                val drawName: String?,

                @SerializedName("drawStatus")
                @Expose
                val drawStatus: String?
            )

            data class TicketDetails(

                @SerializedName("numberOfPanels")
                @Expose
                val numberOfPanels: Int?,

                @SerializedName("productInfo")
                @Expose
                val productInfo: ProductInfo?,

                @SerializedName("saleAmount")
                @Expose
                val saleAmount: Double?,

                @SerializedName("txnCurrency")
                @Expose
                val txnCurrency: String?,

                @SerializedName("winningAmount")
                @Expose
                val winningAmount: Double?,

                @SerializedName("winstatus")
                @Expose
                val winstatus: String?
            ) {
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
    }
}