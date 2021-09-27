package com.zeepos.models.entities

import com.zeepos.models.errors.ApiError

/**
 * Created by Arif S. on 5/3/20
 */
data class ResponseApi<T>(
    var error: ApiError?,
    var data: T?

) {
    var page: Int = 0
    var nextPage: Int = 0
    var totalPages: Int = 0
    var limit: Int = 0
    var totalRecords: Int = 0

    fun isSuccess(): Boolean {
        return error == null && data != null
    }
}