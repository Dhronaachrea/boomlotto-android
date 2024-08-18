package com.skilrock.boomlotto.network

import com.google.gson.GsonBuilder
import com.skilrock.boomlotto.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitNetworking {

    fun create(): ApiCallInterface {

        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(CustomHttpLoggingInterceptor())
            .addInterceptor(NetworkConnectionInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.WEAVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiCallInterface::class.java)
    }

}