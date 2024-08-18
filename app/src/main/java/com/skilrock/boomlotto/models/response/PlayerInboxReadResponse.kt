package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlayerInboxReadResponse(

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("respMsg")
    @Expose
    override val errorMessage: String?,

    @SerializedName("unreadMsgCount")
    @Expose
    val unreadMsgCount: Int?,

    @SerializedName("inboxId")
    @Expose
    var inboxId: Int?,

    @SerializedName("position")
    @Expose
    var position: Int?
) : AppResponse