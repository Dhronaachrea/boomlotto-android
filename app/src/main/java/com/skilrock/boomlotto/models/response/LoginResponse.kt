package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("domainName")
    @Expose
    val domainName: String?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    override val errorMessage: String?,

    @SerializedName("firstDepositCampTrackId")
    @Expose
    val firstDepositCampTrackId: Int?,

    @SerializedName("firstDepositReferSource")
    @Expose
    val firstDepositReferSource: String?,

    @SerializedName("firstDepositReferSourceId")
    @Expose
    val firstDepositReferSourceId: Int?,

    @SerializedName("firstDepositSubSourceId")
    @Expose
    val firstDepositSubSourceId: Int?,

    @SerializedName("mapping")
    @Expose
    val mapping: Mapping?,

    @SerializedName("playerLoginInfo")
    @Expose
    val playerLoginInfo: PlayerLoginInfo?,

    @SerializedName("playerToken")
    @Expose
    val playerToken: String?,

    @SerializedName("profileStatus")
    @Expose
    val profileStatus: String?,

    @SerializedName("ramPlayerInfo")
    @Expose
    val ramPlayerInfo: RamPlayerInfo?,

    @SerializedName("rummyDeepLink")
    @Expose
    val rummyDeepLink: String?
) : AppResponse {
    class Mapping

    data class PlayerLoginInfo(

        @SerializedName("addressVerified")
        @Expose
        val addressVerified: String?,

        @SerializedName("affilateId")
        @Expose
        val affilateId: Int?,

        @SerializedName("ageVerified")
        @Expose
        val ageVerified: String?,

        @SerializedName("autoPassword")
        @Expose
        val autoPassword: String?,

        @SerializedName("avatarPath")
        @Expose
        val avatarPath: String?,

        @SerializedName("commonContentPath")
        @Expose
        val commonContentPath: String?,

        @SerializedName("communicationCharge")
        @Expose
        val communicationCharge: String?,

        @SerializedName("country")
        @Expose
        val country: String?,

        @SerializedName("dob")
        @Expose
        val dob: String?,

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("emailVerified")
        @Expose
        val emailVerified: String?,

        @SerializedName("firstDepositDate")
        @Expose
        val firstDepositDate: String?,

        @SerializedName("firstLoginDate")
        @Expose
        val firstLoginDate: String?,

        @SerializedName("isEmailService")
        @Expose
        val isEmailService: String?,

        @SerializedName("isPlay2x")
        @Expose
        val isPlay2x: String?,

        @SerializedName("isSmsService")
        @Expose
        val isSmsService: String?,

        @SerializedName("lastLoginDate")
        @Expose
        val lastLoginDate: String?,

        @SerializedName("lastLoginIP")
        @Expose
        val lastLoginIP: String?,

        @SerializedName("mobileNo")
        @Expose
        val mobileNo: String?,

        @SerializedName("olaPlayer")
        @Expose
        val olaPlayer: String?,

        @SerializedName("phoneVerified")
        @Expose
        val phoneVerified: String?,

        @SerializedName("playerId")
        @Expose
        val playerId: Int?,

        @SerializedName("playerStatus")
        @Expose
        val playerStatus: String?,

        @SerializedName("playerType")
        @Expose
        val playerType: String?,

        @SerializedName("referFriendCode")
        @Expose
        val referFriendCode: String?,

        @SerializedName("referSource")
        @Expose
        val referSource: String?,

        @SerializedName("regDevice")
        @Expose
        val regDevice: String?,

        @SerializedName("registrationDate")
        @Expose
        val registrationDate: String?,

        @SerializedName("registrationIp")
        @Expose
        val registrationIp: String?,

        @SerializedName("state")
        @Expose
        val state: String?,

        @SerializedName("stateCode")
        @Expose
        val stateCode: String?,

        @SerializedName("city")
        @Expose
        val city: String?,

        @SerializedName("unreadMsgCount")
        @Expose
        val unreadMsgCount: Int?,

        @SerializedName("userName")
        @Expose
        val userName: String?,

        @SerializedName("lastName")
        @Expose
        var lastName: String?,

        @SerializedName("firstName")
        @Expose
        var firstName: String?,

        @SerializedName("emailId")
        @Expose
        var emailId: String?,

        @SerializedName("walletBean")
        @Expose
        val walletBean: WalletBean?
    ) {
        data class WalletBean(

            @SerializedName("bonusBalance")
            @Expose
            var bonusBalance: Double?,

            @SerializedName("cashBalance")
            @Expose
            var cashBalance: Double?,

            @SerializedName("currency")
            @Expose
            var currency: String?,

            @SerializedName("depositBalance")
            @Expose
            var depositBalance: Double?,

            @SerializedName("practiceBalance")
            @Expose
            var practiceBalance: Double?,

            @SerializedName("preferredWallet")
            @Expose
            var preferredWallet: String?,

            @SerializedName("totalBalance")
            @Expose
            var totalBalance: Double?,

            @SerializedName("totalDepositBalance")
            @Expose
            var totalDepositBalance: Double?,

            @SerializedName("totalWithdrawableBalance")
            @Expose
            var totalWithdrawableBalance: Double?,

            @SerializedName("winningBalance")
            @Expose
            var winningBalance: Double?,

            @SerializedName("withdrawableBal")
            @Expose
            var withdrawableBal: Double?,

            @SerializedName("currencyDisplayCode")
            @Expose
            var currencyDisplayCode: String?,

            @SerializedName("totalWinningBalance")
            @Expose
            var totalWinningBalance: Double?
        )
    }

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
        var bankVerified: String?,

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
        var idVerified: String?,

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
        val profileExpiredAt: Any?,

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
        val verificationModeAt: Any?,

        @SerializedName("verifiedBy")
        @Expose
        val verifiedBy: Any?
    )
}