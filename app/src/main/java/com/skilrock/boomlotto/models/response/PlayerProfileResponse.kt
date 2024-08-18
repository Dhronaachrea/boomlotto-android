package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlayerProfileResponse(

    @SerializedName("docUploadStatus")
    @Expose
    val docUploadStatus: String?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?,

    @SerializedName("domainName")
    @Expose
    val domainName: String?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("fistDeposited")
    @Expose
    val fistDeposited: Boolean?,

    @SerializedName("mapping")
    @Expose
    val mapping: LoginResponse.Mapping?,

    @SerializedName("playerInfoBean")
    @Expose
    val playerInfoBean: PlayerInfoBean?,

    @SerializedName("profileStatus")
    @Expose
    val profileStatus: String?,

    @SerializedName("profileUpdate")
    @Expose
    val profileUpdate: Boolean?,

    @SerializedName("ramAddressInfo")
    @Expose
    val ramAddressInfo: RamAddressInfo?,

    @SerializedName("ramPlayerInfo")
    @Expose
    val ramPlayerInfo: LoginResponse.RamPlayerInfo?
) : AppResponse {
    class Mapping

    data class PlayerInfoBean(

        @SerializedName("avatarPath")
        @Expose
        val avatarPath: String?,

        @SerializedName("commonContentPath")
        @Expose
        val commonContentPath: String?,

        @SerializedName("country")
        @Expose
        val country: String?,

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("currencyId")
        @Expose
        val currencyId: Int?,

        @SerializedName("emailId")
        @Expose
        val emailId: String?,

        @SerializedName("emailVerified")
        @Expose
        val emailVerified: String?,

        @SerializedName("isOtpVerified")
        @Expose
        val isOtpVerified: Boolean?,

        @SerializedName("mobileNo")
        @Expose
        val mobileNo: String?,

        @SerializedName("mobileNumber")
        @Expose
        val mobileNumber: String?,

        @SerializedName("phoneVerified")
        @Expose
        val phoneVerified: String?,

        @SerializedName("playerId")
        @Expose
        val playerId: Int?,

        @SerializedName("playerStatus")
        @Expose
        val playerStatus: String?,

        @SerializedName("sms")
        @Expose
        val sms: Boolean?,

        @SerializedName("firstName")
        @Expose
        val firstName: String?,

        @SerializedName("lastName")
        @Expose
        val lastName: String?,

        @SerializedName("userName")
        @Expose
        val userName: String?,

        @SerializedName("ussd")
        @Expose
        val ussd: Boolean?,

        @SerializedName("walletBean")
        @Expose
        val walletBean: WalletBean?
    ) {
        data class WalletBean(

            @SerializedName("bonusBalance")
            @Expose
            val bonusBalance: Double?,

            @SerializedName("cashBalance")
            @Expose
            val cashBalance: Double?,

            @SerializedName("depositBalance")
            @Expose
            val depositBalance: Double?,

            @SerializedName("practiceBalance")
            @Expose
            val practiceBalance: Double?,

            @SerializedName("totalBalance")
            @Expose
            val totalBalance: Double?,

            @SerializedName("totalDepositBalance")
            @Expose
            val totalDepositBalance: Double?,

            @SerializedName("totalWinningBalance")
            @Expose
            val totalWinningBalance: Double?,

            @SerializedName("totalWithdrawableBalance")
            @Expose
            val totalWithdrawableBalance: Double?,

            @SerializedName("winningBalance")
            @Expose
            val winningBalance: Double?,

            @SerializedName("withdrawableBal")
            @Expose
            val withdrawableBal: Double?
        )
    }

    data class RamAddressInfo(

        @SerializedName("addressOne")
        @Expose
        val addressOne: String?,

        @SerializedName("addressTwo")
        @Expose
        val addressTwo: String?,

        @SerializedName("city")
        @Expose
        val city: String?,

        @SerializedName("country")
        @Expose
        val country: String?,

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("state")
        @Expose
        val state: String?,

        @SerializedName("stateCode")
        @Expose
        val stateCode: String?,

        @SerializedName("town")
        @Expose
        val town: String?,

        @SerializedName("zip")
        @Expose
        val zip: String?
    )

    data class RamPlayerInfo(

        @SerializedName("addressVerified")
        @Expose
        val addressVerified: String?,

        @SerializedName("addressVerifiedAt")
        @Expose
        val addressVerifiedAt: String?,

        @SerializedName("ageVerified")
        @Expose
        val ageVerified: String?,

        @SerializedName("ageVerifiedAt")
        @Expose
        val ageVerifiedAt: String?,

        @SerializedName("bankVerified")
        @Expose
        val bankVerified: String?,

        @SerializedName("bankVerifiedAt")
        @Expose
        val bankVerifiedAt: String?,

        @SerializedName("createdAt")
        @Expose
        val createdAt: String?,

        @SerializedName("docUploaded")
        @Expose
        val docUploaded: String?,

        @SerializedName("domainId")
        @Expose
        val domainId: Int?,

        @SerializedName("emailVerified")
        @Expose
        val emailVerified: String?,

        @SerializedName("emailVerifiedAt")
        @Expose
        val emailVerifiedAt: String?,

        @SerializedName("id")
        @Expose
        val id: Int?,

        @SerializedName("idVerified")
        @Expose
        val idVerified: String?,

        @SerializedName("idVerifiedAt")
        @Expose
        val idVerifiedAt: String?,

        @SerializedName("merchantId")
        @Expose
        val merchantId: Int?,

        @SerializedName("mobileVerified")
        @Expose
        val mobileVerified: String?,

        @SerializedName("mobileVerifiedAt")
        @Expose
        val mobileVerifiedAt: String?,

        @SerializedName("nameVerified")
        @Expose
        val nameVerified: String?,

        @SerializedName("playerId")
        @Expose
        val playerId: Int?,

        @SerializedName("profileExpiredAt")
        @Expose
        val profileExpiredAt: String?,

        @SerializedName("profileStatus")
        @Expose
        val profileStatus: String?,

        @SerializedName("securityQuestionVerified")
        @Expose
        val securityQuestionVerified: String?,

        @SerializedName("securityQuestionVerifiedAt")
        @Expose
        val securityQuestionVerifiedAt: String?,

        @SerializedName("taxationIdVerified")
        @Expose
        val taxationIdVerified: String?,

        @SerializedName("taxationIdVerifiedAt")
        @Expose
        val taxationIdVerifiedAt: String?,

        @SerializedName("updatedAt")
        @Expose
        val updatedAt: String?,

        @SerializedName("uploadPendingDate")
        @Expose
        val uploadPendingDate: Any?,

        @SerializedName("verificationAssignAt")
        @Expose
        val verificationAssignAt: Any?,

        @SerializedName("verificationModeAt")
        @Expose
        val verificationModeAt: String?,

        @SerializedName("verifiedBy")
        @Expose
        val verifiedBy: Any?
    )
}