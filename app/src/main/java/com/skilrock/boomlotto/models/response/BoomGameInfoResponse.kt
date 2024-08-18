package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BoomGameInfoResponse(

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

            @SerializedName("games")
            @Expose
            val games: Games?,

            @SerializedName("serverTime")
            @Expose
            val serverTime: ServerTime?
    ) {
        data class Games(

                @SerializedName("DAILYLOTTO")
                @Expose
                val dAILYLOTTO: DAILYLOTTO?
        ) {
            data class DAILYLOTTO(

                    @SerializedName("active")
                    @Expose
                    val active: String?,

                    @SerializedName("content")
                    @Expose
                    val content: Content?,

                    @SerializedName("datetime")
                    @Expose
                    val datetime: String?,

                    @SerializedName("donation")
                    @Expose
                    val donation: List<Donation?>?,

                    @SerializedName("draw_date")
                    @Expose
                    val drawDate: String?,

                    @SerializedName("estimated_jackpot")
                    @Expose
                    val estimatedJackpot: String?,

                    @SerializedName("game_code")
                    @Expose
                    val gameCode: String?,

                    @SerializedName("guaranteed_jackpot")
                    @Expose
                    val guaranteedJackpot: String?,

                    @SerializedName("jackpot_amount")
                    @Expose
                    val jackpotAmount: String?,

                    @SerializedName("jackpot_title")
                    @Expose
                    val jackpotTitle: String?,

                    @SerializedName("next_draw_date")
                    @Expose
                    val nextDrawDate: String?
            ) {
                data class Content(

                        @SerializedName("currentDrawFreezeDate")
                        @Expose
                        val currentDrawFreezeDate: String?,

                        @SerializedName("currentDrawNumber")
                        @Expose
                        val currentDrawNumber: Int?,

                        @SerializedName("currentDrawStopTime")
                        @Expose
                        val currentDrawStopTime: String?,

                        @SerializedName("jackpotAmount")
                        @Expose
                        val jackpotAmount: Double?,

                        @SerializedName("unitCostJson")
                        @Expose
                        val unitCostJson: List<UnitCostJson?>?
                ) {
                    data class UnitCostJson(

                            @SerializedName("currency")
                            @Expose
                            val currency: String?,

                            @SerializedName("price")
                            @Expose
                            val price: Double?
                    )
                }

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

        data class ServerTime(

                @SerializedName("date")
                @Expose
                val date: String?,

                @SerializedName("timezone")
                @Expose
                val timezone: String?,

                @SerializedName("timezone_type")
                @Expose
                val timezoneType: Int?
        )
    }
}