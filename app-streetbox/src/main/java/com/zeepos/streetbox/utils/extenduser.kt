package com.zeepos.streetbox.utils

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.zeepos.models.response.ResponseError
import retrofit2.adapter.rxjava2.HttpException
import java.io.IOException

fun showErrorMessageThrowable(throwable: Throwable?): String {
    var message: String = ""
    if (throwable is HttpException) {
        val body = throwable.response()?.errorBody()
        val gson = Gson()
        val adapter: TypeAdapter<ResponseError> = gson.getAdapter(ResponseError::class.java)
        try {
            val errorResponse: ResponseError = adapter.fromJson(body?.string())
            message = errorResponse.error?.message.toString()
            println("respon Message $message")
        } catch (e: IOException) {
        }
    }
    return message
}