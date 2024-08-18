package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BoomLottoGameResponse(

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

        @SerializedName("currentDate")
        @Expose
        val currentDate: String?,

        @SerializedName("gameRespVOs")
        @Expose
        val gameRespVOs: List<GameRespVO?>?
    ) {
        data class GameRespVO(

            @SerializedName("betLimitEnabled")
            @Expose
            val betLimitEnabled: String?,

            @SerializedName("betRespVOs")
            @Expose
            val betRespVOs: List<BetRespVO?>?,

            @SerializedName("consecutiveDraw")
            @Expose
            val consecutiveDraw: String?,

            @SerializedName("displayOrder")
            @Expose
            val displayOrder: String?,

            @SerializedName("donation")
            @Expose
            val donation: List<Donation?>?,

            @SerializedName("drawEvent")
            @Expose
            val drawEvent: String?,

            @SerializedName("drawFrequencyType")
            @Expose
            val drawFrequencyType: String?,

            @SerializedName("drawPrizeMultiplier")
            @Expose
            val drawPrizeMultiplier: DrawPrizeMultiplier?,

            @SerializedName("drawRespVOs")
            @Expose
            val drawRespVOs: List<DrawRespVO?>?,

            @SerializedName("familyCode")
            @Expose
            val familyCode: String?,

            @SerializedName("gameCode")
            @Expose
            val gameCode: String?,

            @SerializedName("gameName")
            @Expose
            val gameName: String?,

            @SerializedName("gameNumber")
            @Expose
            val gameNumber: Int?,

            @SerializedName("gameOrder")
            @Expose
            val gameOrder: String?,

            @SerializedName("gameSchemas")
            @Expose
            val gameSchemas: GameSchemas?,

            @SerializedName("gameStatus")
            @Expose
            val gameStatus: String?,

            @SerializedName("id")
            @Expose
            val id: Int?,

            @SerializedName("jackpotAmount")
            @Expose
            val jackpotAmount: Double?,

            @SerializedName("lastDrawDateTime")
            @Expose
            val lastDrawDateTime: String?,

            @SerializedName("lastDrawFreezeTime")
            @Expose
            val lastDrawFreezeTime: String?,

            @SerializedName("lastDrawResult")
            @Expose
            val lastDrawResult: String?,

            @SerializedName("lastDrawSaleStopTime")
            @Expose
            val lastDrawSaleStopTime: String?,

            @SerializedName("lastDrawTime")
            @Expose
            val lastDrawTime: String?,

            @SerializedName("lastDrawWinningResultVOs")
            @Expose
            val lastDrawWinningResultVOs: List<LastDrawWinningResultVO?>?,

            @SerializedName("maxAdvanceDraws")
            @Expose
            val maxAdvanceDraws: Int?,

            @SerializedName("maxPanelAllowed")
            @Expose
            val maxPanelAllowed: Int?,

            @SerializedName("nativeCurrency")
            @Expose
            val nativeCurrency: String?,

            @SerializedName("numberConfig")
            @Expose
            val numberConfig: NumberConfig?,

            @SerializedName("resultConfigData")
            @Expose
            val resultConfigData: ResultConfigData?,

            @SerializedName("ticket_expiry")
            @Expose
            val ticketExpiry: Int?,

            @SerializedName("timeToFetchUpdatedGameInfo")
            @Expose
            val timeToFetchUpdatedGameInfo: String?,

            @SerializedName("unitCost")
            @Expose
            val unitCost: List<UnitCost?>?
        ) {
            data class UnitCost(

                @SerializedName("currency")
                @Expose
                val currency: String?,

                @SerializedName("price")
                @Expose
                val price: Double?
            )

            data class BetRespVO(

                @SerializedName("betCode")
                @Expose
                val betCode: String?,

                @SerializedName("betDispName")
                @Expose
                val betDispName: String?,

                @SerializedName("betGroup")
                @Expose
                val betGroup: Any?,

                @SerializedName("betName")
                @Expose
                val betName: String?,

                @SerializedName("betOrder")
                @Expose
                val betOrder: Int?,

                @SerializedName("inputCount")
                @Expose
                val inputCount: String?,

                @SerializedName("maxBetAmtMul")
                @Expose
                val maxBetAmtMul: Int?,

                @SerializedName("pickTypeData")
                @Expose
                val pickTypeData: PickTypeData?,

                @SerializedName("unitPrice")
                @Expose
                val unitPrice: Double?,

                @SerializedName("winMode")
                @Expose
                val winMode: String?
            ) {
                data class PickTypeData(

                    @SerializedName("pickType")
                    @Expose
                    val pickType: List<PickType?>?
                ) {
                    data class PickType(

                        @SerializedName("code")
                        @Expose
                        val code: String?,

                        @SerializedName("coordinate")
                        @Expose
                        val coordinate: Any?,

                        @SerializedName("description")
                        @Expose
                        val description: String?,

                        @SerializedName("name")
                        @Expose
                        val name: String?,

                        @SerializedName("range")
                        @Expose
                        val range: List<Range?>?
                    ) {
                        data class Range(

                            @SerializedName("pickConfig")
                            @Expose
                            val pickConfig: String?,

                            @SerializedName("pickCount")
                            @Expose
                            val pickCount: String?,

                            @SerializedName("pickMode")
                            @Expose
                            val pickMode: String?,

                            @SerializedName("pickValue")
                            @Expose
                            val pickValue: String?,

                            @SerializedName("qpAllowed")
                            @Expose
                            val qpAllowed: String?
                        )
                    }
                }
            }

            data class Donation(

                @SerializedName("image")
                @Expose
                val image: String?,

                @SerializedName("title")
                @Expose
                val title: String?
            )

            data class DrawPrizeMultiplier(

                @SerializedName("applyOnBet")
                @Expose
                val applyOnBet: String?,

                @SerializedName("createEvent")
                @Expose
                val createEvent: Any?,

                @SerializedName("multiplier")
                @Expose
                val multiplier: Any?
            )

            data class DrawRespVO(

                @SerializedName("drawDateTime")
                @Expose
                val drawDateTime: String?,

                @SerializedName("drawDay")
                @Expose
                val drawDay: String?,

                @SerializedName("drawFreezeTime")
                @Expose
                val drawFreezeTime: String?,

                @SerializedName("drawId")
                @Expose
                val drawId: Int?,

                @SerializedName("drawName")
                @Expose
                val drawName: String?,

                @SerializedName("drawSaleStartTime")
                @Expose
                val drawSaleStartTime: String?,

                @SerializedName("drawSaleStopTime")
                @Expose
                val drawSaleStopTime: String?,

                @SerializedName("drawStatus")
                @Expose
                val drawStatus: String?,

                var isSelected: Boolean = false
            )

            data class GameSchemas(

                @SerializedName("gameDevName")
                @Expose
                val gameDevName: String?,

                @SerializedName("matchDetail")
                @Expose
                val matchDetail: List<MatchDetail?>?
            ) {
                data class MatchDetail(

                    @SerializedName("betType")
                    @Expose
                    val betType: String?,

                    @SerializedName("match")
                    @Expose
                    val match: String?,

                    @SerializedName("pattern")
                    @Expose
                    val pattern: Any?,

                    @SerializedName("prizeAmount")
                    @Expose
                    val prizeAmount: String?,

                    @SerializedName("rank")
                    @Expose
                    val rank: Int?,

                    @SerializedName("slabInfo")
                    @Expose
                    val slabInfo: Any?,

                    @SerializedName("type")
                    @Expose
                    val type: String?
                )
            }

            data class LastDrawWinningResultVO(

                @SerializedName("currentDrawStatus")
                @Expose
                val currentDrawStatus: Any?,

                @SerializedName("drawId")
                @Expose
                val drawId: Int?,

                @SerializedName("lastDrawDateTime")
                @Expose
                val lastDrawDateTime: String?,

                @SerializedName("runTimeFlagInfo")
                @Expose
                val runTimeFlagInfo: List<Any?>?,

                @SerializedName("sideBetMatchInfo")
                @Expose
                val sideBetMatchInfo: List<Any?>?,

                @SerializedName("winningMultiplierInfo")
                @Expose
                val winningMultiplierInfo: WinningMultiplierInfo?,

                @SerializedName("winningNumber")
                @Expose
                val winningNumber: String?
            ) {
                data class WinningMultiplierInfo(

                    @SerializedName("multiplierCode")
                    @Expose
                    val multiplierCode: Any?,

                    @SerializedName("value")
                    @Expose
                    val value: Any?
                )
            }

            data class NumberConfig(

                @SerializedName("range")
                @Expose
                val range: List<Range?>?
            ) {
                data class Range(

                    @SerializedName("ball")
                    @Expose
                    val ball: ArrayList<Ball?>?
                ) {
                    data class Ball(

                        @SerializedName("color")
                        @Expose
                        val color: String?,

                        @SerializedName("label")
                        @Expose
                        val label: String?,

                        @SerializedName("number")
                        @Expose
                        val number: String?,

                        var isClicked: Boolean = false,

                        var isResetAnimation: Boolean = false,

                        var isQuickPickAnimation: Boolean = false
                    )
                }
            }

            data class ResultConfigData(

                @SerializedName("balls")
                @Expose
                val balls: String?,

                @SerializedName("ballsPerCall")
                @Expose
                val ballsPerCall: Int?,

                @SerializedName("duplicateAllowed")
                @Expose
                val duplicateAllowed: Boolean?,

                @SerializedName("interval")
                @Expose
                val interval: Int?,

                @SerializedName("type")
                @Expose
                val type: String?
            )
        }
    }
}