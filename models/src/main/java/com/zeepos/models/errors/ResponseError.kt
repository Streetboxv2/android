package com.zeepos.models.errors

/**
 * Created by Arif S. on 5/1/20
 */
class ResponseError : AppError {
    constructor(error: Throwable) : super(error)

    constructor(code: Int, errorBody: String) : super("error: $code, errorBody: $errorBody")
}