package com.zeepos.models.response

class ResponseError {
    var error: Error? = null
}

class Error {
    var code: Int = 0
    var message: String = ""
}