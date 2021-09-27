package com.zeepos.models.errors

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 5/18/20
 */
class ApiError {
    var code: Int = 0
    var message: String? = ConstVar.EMPTY_STRING
}