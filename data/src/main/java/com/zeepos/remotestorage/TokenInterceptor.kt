package com.zeepos.remotestorage

import android.util.Log
import com.zeepos.localstorage.Storage
import com.zeepos.models.ConstVar
import com.zeepos.models.master.UserAuthData
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Arif S. on 5/18/20
 */
@Singleton
class TokenInterceptor @Inject constructor(
    private val storage: Storage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        val originalHttpUrl = original.url

        if (original.url.encodedPath.contains("login") && original.method
                .toLowerCase() == "post"
            || (original.url.encodedPath.contains("register") && original.method
                .toLowerCase() == "post")
        ) {
            return chain.proceed(original)
        }

        val token = storage[ConstVar.USER_AUTH, UserAuthData::class.java]?.accessToken
            ?: ConstVar.EMPTY_STRING

        Log.d(ConstVar.TAG, "Token -> $token")

        val requestBuilder = original.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .url(originalHttpUrl)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}