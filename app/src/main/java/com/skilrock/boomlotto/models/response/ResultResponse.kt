package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResultResponse(

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

        @SerializedName("lastDrawId")
        @Expose
        val lastDrawId: Int?,

        @SerializedName("resultData")
        @Expose
        val resultData: ArrayList<ResultData?>?
    ) {
        data class ResultData(

            @SerializedName("resultDate")
            @Expose
            val resultDate: String?,

            @SerializedName("resultInfo")
            @Expose
            val resultInfo: ArrayList<ResultInfo?>?
        ) {
            data class ResultInfo(

                @SerializedName("drawId")
                @Expose
                val drawId: Int?,

                @SerializedName("drawName")
                @Expose
                val drawName: String?,

                @SerializedName("drawTime")
                @Expose
                val drawTime: String?,

                @SerializedName("matchInfo")
                @Expose
                val matchInfo: ArrayList<MatchInfo?>?,

                @SerializedName("runTimeFlagInfo")
                @Expose
                val runTimeFlagInfo: Any?,

                @SerializedName("sideBetMatchInfo")
                @Expose
                val sideBetMatchInfo: ArrayList<Any?>?,

                @SerializedName("videoUrl")
                @Expose
                val videoUrl: String?,

                @SerializedName("winningMultiplierInfo")
                @Expose
                val winningMultiplierInfo: Any?,

                @SerializedName("winningNo")
                @Expose
                val winningNo: String?,

                var isExpanded: Boolean = false,

                var rowIndex: Int,

                var resultDate: String?
            ) {
                data class MatchInfo(

                    @SerializedName("amount")
                    @Expose
                    val amount: String?,

                    @SerializedName("match")
                    @Expose
                    val match: String?,

                    @SerializedName("mode")
                    @Expose
                    val mode: String?,

                    @SerializedName("noOfWinners")
                    @Expose
                    val noOfWinners: String?,

                    @SerializedName("prizeRank")
                    @Expose
                    val prizeRank: Int?,

                    var rowIndex: Int = -1
                )
            }
        }
    }
}