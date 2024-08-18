package com.skilrock.boomlotto.models.response


import android.annotation.SuppressLint
import android.text.format.DateUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat

data class PlayerInboxResponse(

    @SerializedName("contentPath")
    @Expose
    val contentPath: String?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("plrInboxList")
    @Expose
    val plrInboxList: ArrayList<PlrInbox?>?,

    @SerializedName("respMsg")
    @Expose
    override val errorMessage: String?,

    @SerializedName("unreadMsgCount")
    @Expose
    val unreadMsgCount: Int?
) : AppResponse {
    data class PlrInbox(

        @SerializedName("contentId")
        @Expose
        val contentId: String?,

        @SerializedName("content_id")
        @Expose
        val contentIdForUse: String?,

        @SerializedName("from")
        @Expose
        val from: String?,

        @SerializedName("inboxId")
        @Expose
        val inboxId: Int?,

        @SerializedName("mailSentDate")
        @Expose
        val mailSentDate: String?,

        @SerializedName("stared")
        @Expose
        val stared: Boolean?,

        @SerializedName("status")
        @Expose
        var status: String?,

        @SerializedName("subject")
        @Expose
        val subject: String?,

        @SerializedName("isSelectedByLongPress")
        @Expose
        var isSelectedByLongPress: Boolean = false
    ) {
        @SuppressLint("SimpleDateFormat")
        fun getDateFormatted() : String {
            return mailSentDate?.let { strDate ->
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDate)
                date?.let {
                    when {
                        DateUtils.isToday(it.time) -> {
                            getTime()
                        }
                        DateUtils.isToday(it.time + DateUtils.DAY_IN_MILLIS) -> {
                            "Yesterday"
                        }
                        else -> {
                            getDate()
                        }
                    }
                } ?: "NA"
            } ?: "NA"
        }

        @SuppressLint("SimpleDateFormat")
        fun getDate() : String {
            return try {
                val parser = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                val formatter = SimpleDateFormat("MMM dd, yyyy")
                mailSentDate?.let { strDate ->
                    val date = parser.parse(strDate)
                    date?.let {
                        formatter.format(it)
                    }
                } ?: "N.A."
            } catch (e: Exception) {
                "NA"
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime() : String {
            return try {
                val parser = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                val formatter = SimpleDateFormat("hh:mm aa")
                mailSentDate?.let { strDate ->
                    val date = parser.parse(strDate)
                    date?.let {
                        formatter.format(it)
                    }
                } ?: "N.A."
            } catch (e: Exception) {
                "NA"
            }
        }

        fun isMsgRead() : Boolean {
            return status.equals("READ", true)
        }

        fun getFormattedMsg() : String {
            return contentIdForUse?.replace("\r\n", " ") ?: ""
        }
    }
}