package com.streetbox.pos.di

import android.provider.Telephony.Carriers.SERVER
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.streetbox.pos.BuildConfig
import com.zeepos.remotestorage.TokenInterceptor
import com.zeepos.utilities.SslUtils
import com.zeepos.utilities.gson.HiddenAnnotationExclusionStrategy
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.net.ssl.X509TrustManager

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

        builder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader(
                    "CLIENT_ID", BuildConfig.CLIENT_ID
                ).build()
            chain.proceed(request)
        }

        builder.sslSocketFactory(
            SslUtils.getUnSaveSslContext().socketFactory,
            SslUtils.getTrustManager()[0] as X509TrustManager
        )
        builder.hostnameVerifier { _, _ -> true }
//        builder.authenticator(tokenAuthenticator)
        builder.addInterceptor(tokenInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson =
        GsonBuilder().addSerializationExclusionStrategy(HiddenAnnotationExclusionStrategy())
            .addDeserializationExclusionStrategy(HiddenAnnotationExclusionStrategy()).create()

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