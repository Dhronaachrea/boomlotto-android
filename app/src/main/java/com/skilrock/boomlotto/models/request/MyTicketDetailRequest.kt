package com.skilrock.boomlotto.models.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.boomlotto.utility.PlayerInfo
import com.skilrock.boomlotto.utility.WEAVER

data class MyTicketDetailRequest(

    @SerializedName("merchantCode")
    @Expose
    val merchantCode: String? = WEAVER,

    @SerializedName("merchantTxnId")
    @Expose
    val merchantTxnId: String? = "0",

    @SerializedName("playerId")
    @Expose
    val playerId: String? = PlayerInfo.getPlayerId().toString(),

    @SerializedName("ticketNumber")
    @Expose
    val ticketNumber: String?
)