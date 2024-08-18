package com.skilrock.boomlotto.utility

import android.content.Context
import com.google.gson.Gson
import com.skilrock.boomlotto.models.response.InstantGameListResponse

object SharedPrefUtils {

    private const val USER_PREF         = "BOOM"
    private const val APP_PREF          = "AppPref"
    const val PLAYER_TOKEN              = "playerToken"
    const val PLAYER_USER_NAME          = "playerUserName"
    const val PLAYER_ID                 = "playerId"
    const val PLAYER_MOBILE_NUMBER      = "playerMobileNo"
    const val PLAYER_DATA               = "playerData"

    fun getString(context: Context, key: String): String {
        return context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).getString(key, "") ?: ""
    }

    fun setString(context: Context, key: String, value: String) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putString(key, value).apply()
    }

    fun getBanner(context: Context): ArrayList<String>? {
        val stringSet = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getStringSet("banner", null)
        if (stringSet != null && stringSet.isNotEmpty()) {
            val bannerList = ArrayList<String>()
            for (index in 0 until 5)
                bannerList.addAll(stringSet)
            return bannerList
        }
        return null
    }

    fun setBanner(context: Context, value: LinkedHashSet<String>) {
        context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putStringSet("banner", value).apply()
    }

    fun setInstantGameListResponse(context: Context, value: String) {
        context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString("instantGameList", value).apply()
    }

    fun getInstantGameListResponse(context: Context) : InstantGameListResponse? {
        val response = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString("instantGameList", "")
        return if (response == null || response.isBlank())
            null
        else
            Gson().fromJson(response, InstantGameListResponse::class.java)
    }

    fun getInt(context: Context, key: String): Int {
        return context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).getInt(key, -1)
    }

    fun setInt(context: Context, key: String, value: Int) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putInt(key, value).apply()
    }

    fun setBadgeCount(context: Context, value: Int) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putInt("badgeCount", value).apply()
    }

    fun getBadgeCount(context: Context): Int {
        return context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).getInt("badgeCount", 0)
    }

    fun clearAppSharedPref(context: Context) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun setFcmToken(context: Context, value: String) {
        context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString("fcmToken", value).apply()
    }

    fun getFcmToken(context: Context): String {
        return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString("fcmToken", "") ?: ""
    }

    fun setBoomUnitPrice(context: Context, value: String) {
        context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString("boomUnitPrice", value).apply()
    }

    fun getBoomUnitPrice(context: Context): String {
        return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString("boomUnitPrice", "") ?: ""
    }

    fun setAppLanguage(context: Context, value: String) {
        context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString("appLanguage", value).apply()
    }

    fun getAppLanguage(context: Context): String {
        return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString("appLanguage", LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
    }

    fun setHomePageRedirection(context: Context, value: String) {
        context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString("homePageRedirection", value).apply()
    }

    fun getHomePageRedirection(context: Context): String {
        return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString("homePageRedirection", "") ?: ""
    }

}