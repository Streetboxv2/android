package com.zeepos.networkmaps

import com.zeepos.remotestorage.RemoteService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClientMaps {
    companion object {
        var retrofit: Retrofit? = null
        private var apiServices: RemoteService? = null

        fun ApiClient(): RemoteService? {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiServices = retrofit.create(RemoteService::class.java)
            return apiServices
        }


        fun provideOkHttpClient(): OkHttpClient? {
            return OkHttpClient.Builder()
                .addInterceptor(getLoggingInterceptor())
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build()
        }

        fun getLoggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor()
            return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
}