package com.zeepos.models.errors

import android.annotation.TargetApi
import android.os.Build

/**
 * Created by Arif S. on 5/1/20
 */
open class AppError : Exception {
    constructor()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)

    @TargetApi(Build.VERSION_CODES.N)
    constructor(
        message: String, cause: Throwable, enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, cause, enableSuppression, writableStackTrace)
}