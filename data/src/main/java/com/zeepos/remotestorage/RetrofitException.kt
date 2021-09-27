package com.zeepos.remotestorage

import com.zeepos.models.entities.ResponseApi
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException

/**
 * Created by Arif S. on 6/16/20
 */
object RetrofitException {
    fun handleRetrofitException(throwable: Throwable, retrofit: Retrofit): Throwable {
        if (throwable is HttpException) {
            val response = throwable.response()
            val error =
                getErrorBodyAs(response.errorBody(), retrofit, ResponseApi::class.java)?.error
            val errorMessage = error?.message ?: throwable.message

            return Throwable(errorMessage, throwable)
        }

        return throwable
    }

    @Throws(IOException::class)
    private fun <T> getErrorBodyAs(
        errorBody: ResponseBody?,
        retrofit: Retrofit,
        type: Class<T>
    ): T? {
        if (errorBody != null) {
            val converter: Converter<ResponseBody, T> =
                retrofit.responseBodyConverter(type, arrayOfNulls<Annotation>(0))
            return converter.convert(errorBody)
        }

        return null
    }
}