package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BoomGameInfoResponseTrash(

    @SerializedName("data")
    @Expose
    val `data`: Data?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("message")
    @Expose
    val message: String?
) {
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

                @SerializedName("datetime")
                @Expose
                val datetime: String?,

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
            )
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