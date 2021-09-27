package com.zeepos.recruiter.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.maps.GeoApiContext
import com.zeepos.recruiter.App
import com.zeepos.recruiter.BuildConfig
import com.zeepos.recruiter.R
import com.zeepos.remotestorage.TokenInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Arif S. on 5/2/20
 */
@Module(includes = [StorageModule::class])
class DomainToolsModule {
    @Provides
    @Singleton
    internal fun okHttpClient(
        tokenInterceptor: TokenInterceptor
//        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }

//        builder.authenticator(tokenAuthenticator)
        builder.addInterceptor(tokenInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    internal fun provideRestAdapter(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        val builder = Retrofit.Builder().client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        builder.baseUrl(BuildConfig.SERVER)
        return builder.build()
    }

}