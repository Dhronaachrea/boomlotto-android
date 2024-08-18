package com.skilrock.boomlotto.utility

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.models.response.BalanceResponse
import com.skilrock.boomlotto.models.response.HeaderInfoResponse
import com.skilrock.boomlotto.models.response.LoginResponse
import java.text.SimpleDateFormat

object PlayerInfo {

    private var loginData: LoginResponse? = null

    fun setLoginData(loginResponse: LoginResponse) {
        loginData = loginResponse
    }

    fun getLoginData() : LoginResponse? {
        return loginData
    }

    fun destroy() {
        loginData = null
    }

    fun setLoginData(context: Context, loginResponse: LoginResponse) {
        loginData = loginResponse
        SharedPrefUtils.setInt(context, SharedPrefUtils.PLAYER_ID, loginResponse.playerLoginInfo?.playerId ?: 0)
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_TOKEN, loginResponse.playerToken ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_USER_NAME, loginResponse.playerLoginInfo?.userName ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_MOBILE_NUMBER, loginResponse.playerLoginInfo?.mobileNo ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginResponse))
    }

    fun setBalance(context: Context, wallet: BalanceResponse.Wallet) {
        loginData?.playerLoginInfo?.walletBean?.let {
            it.bonusBalance             = wallet.bonusBalance
            it.cashBalance              = wallet.cashBalance
            it.currency                 = wallet.currency
            it.depositBalance           = wallet.depositBalance
            it.practiceBalance          = wallet.practiceBalance
            it.preferredWallet          = wallet.preferredWallet
            it.totalBalance             = wallet.totalBalance
            it.totalDepositBalance      = wallet.totalDepositBalance
            it.totalWithdrawableBalance = wallet.totalWithdrawableBalance
            it.winningBalance           = wallet.winningBalance
            it.withdrawableBal          = wallet.withdrawableBal
            it.totalWinningBalance      = wallet.totalWinningBalance
            SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
        }
    }

    fun setBalance(context: Context, headerInfoResponse: HeaderInfoResponse) {
        loginData?.playerLoginInfo?.walletBean?.let {
            it.cashBalance              = headerInfoResponse.cashbal
            it.withdrawableBal          = headerInfoResponse.withdrawableBal
            SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
        }
    }

    fun setEmailId(context: Context, email: String) {
        loginData?.playerLoginInfo?.emailId = email
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
    }

    fun getRawBalance() : Double {
        return loginData?.playerLoginInfo?.walletBean?.cashBalance ?: 0.0
    }

    fun getRawWithdrawalAbleBalance() : Double {
        return loginData?.playerLoginInfo?.walletBean?.withdrawableBal ?: 0.0
    }

    fun getPlayerWithdrawalAbleBalance() : String {
        val balance = loginData?.playerLoginInfo?.walletBean?.withdrawableBal ?: 0.0
        //return BuildConfig.CURRENCY_CODE + " " + String.format("%.2f", balance)
        return BuildConfig.CURRENCY_CODE + " " + getFormattedAmount(balance)
    }

    fun getPlayerTotalBalance() : String {
        val balance = loginData?.playerLoginInfo?.walletBean?.cashBalance ?: 0.0
        //return BuildConfig.CURRENCY_CODE + " " + String.format("%.2f", balance)
        return BuildConfig.CURRENCY_CODE + " " + getFormattedAmount(balance)
    }

    fun getPlayerTotalBalanceBold() : String {
        val balance = loginData?.playerLoginInfo?.walletBean?.cashBalance ?: 0.0
        //return BuildConfig.CURRENCY_CODE + " <b>" + String.format("%.2f", balance) + "</b>"
        return BuildConfig.CURRENCY_CODE + " <b>" + getFormattedAmount(balance) + "</b>"
    }

    fun getBadgeCount(context: Context) : Int {
        return SharedPrefUtils.getBadgeCount(context)
    }

    fun setBadgeCount(context: Context, count: Int) {
        SharedPrefUtils.setBadgeCount(context, count)
    }

    fun getDateOfBirth(context: Context) : String {
        loginData?.playerLoginInfo?.dob?.let { dob ->
            return getDateToShow(context, dob)
        } ?: run { return "" }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateToShow(context: Context, strDate: String): String {
        return try {
            val parser      = SimpleDateFormat("dd/MM/yyyy")
            val formatter   = SimpleDateFormat("MMM d, yyyy")
            parser.parse(strDate)?.let { formatter.format(it) } ?: context.getString(R.string.na)
        } catch (e: Exception) {
            ""
        }
    }

    fun setBankVerified(context: Context) {
        loginData?.ramPlayerInfo?.bankVerified = "UPLOADED"
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
    }

    fun setPlayerFirstName(context: Context, firstName: String) {
        loginData?.playerLoginInfo?.firstName = firstName
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
    }

    fun setPlayerLastName(context: Context, lastName: String) {
        loginData?.playerLoginInfo?.lastName = lastName
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
    }

    fun getPlayerId() : Int = loginData?.playerLoginInfo?.playerId ?: 0
    fun getPlayerToken() : String = loginData?.playerToken ?: ""
    fun getPlayerUserName() : String = loginData?.playerLoginInfo?.userName ?: ""
    fun getPlayerMobileNumber() : String = loginData?.playerLoginInfo?.mobileNo ?: ""
    fun getPlayerFirstName() : String = loginData?.playerLoginInfo?.firstName?.replaceFirstChar { it.uppercase() } ?: ""
    fun getPlayerEmailId() : String = loginData?.playerLoginInfo?.emailId ?: ""
    fun getPlayerLastName() : String = loginData?.playerLoginInfo?.lastName?.replaceFirstChar { it.uppercase() } ?: ""
    fun getCurrency() : String = loginData?.playerLoginInfo?.walletBean?.currency ?: BuildConfig.CURRENCY_CODE
    fun getCurrencyDisplayCode() : String = loginData?.playerLoginInfo?.walletBean?.currencyDisplayCode ?: BuildConfig.CURRENCY_CODE
    fun getProfilePicPath() : String = loginData?.playerLoginInfo?.commonContentPath + loginData?.playerLoginInfo?.avatarPath.toString()
    fun getPlayerInfo() : LoginResponse? = loginData
    fun isIdVerified() : Boolean = !loginData?.ramPlayerInfo?.idVerified.equals("PENDING", true)
    fun isBankVerified() : Boolean = !loginData?.ramPlayerInfo?.bankVerified.equals("PENDING", true)
    fun getReferFriendCode() : String = loginData?.playerLoginInfo?.referFriendCode ?: ""
}