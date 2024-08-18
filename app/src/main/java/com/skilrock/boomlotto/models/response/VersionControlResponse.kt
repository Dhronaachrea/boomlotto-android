package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VersionControlResponse(

    @SerializedName("appDetails")
    @Expose
    val appDetails: AppDetails?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("gameEngineInfo")
    @Expose
    val gameEngineInfo: GameEngineInfo?,

    @SerializedName("isplayerLogin")
    @Expose
    val isplayerLogin: Boolean?,

    @SerializedName("respMsg")
    @Expose
    override val errorMessage: String?,

    @SerializedName("staticLogoDisplay")
    @Expose
    val staticLogoDisplay: Boolean?
) : AppResponse {
    data class AppDetails(

        @SerializedName("appType")
        @Expose
        val appType: String?,

        @SerializedName("isUpdateAvailable")
        @Expose
        val isUpdateAvailable: Boolean?,

        @SerializedName("mandatory")
        @Expose
        val mandatory: Boolean?,

        @SerializedName("message")
        @Expose
        val message: String?,

        @SerializedName("os")
        @Expose
        val os: String?,

        @SerializedName("url")
        @Expose
        val url: String?,

        @SerializedName("version")
        @Expose
        val version: String?,

        @SerializedName("versionCode")
        @Expose
        val versionCode: String?,

        @SerializedName("versionDate")
        @Expose
        val versionDate: String?,

        @SerializedName("version_type")
        @Expose
        val versionType: String?
    )

    data class GameEngineInfo(

        @SerializedName("CAP")
        @Expose
        val cAP: CAP?,

        @SerializedName("DGE")
        @Expose
        val dGE: DGE?,

        @SerializedName("IGE")
        @Expose
        val iGE: IGE?,

        @SerializedName("SBS")
        @Expose
        val sBS: SBS?,

        @SerializedName("SGE")
        @Expose
        val sGE: SGE?,

        @SerializedName("SLE")
        @Expose
        val sLE: SLE?
    ) {
        data class CAP(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class DGE(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class IGE(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class SBS(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class SGE(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class SLE(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )
    }
}