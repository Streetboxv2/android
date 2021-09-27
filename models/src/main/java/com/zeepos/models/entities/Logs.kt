package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 7/1/20
 */
class Logs {
    var id: Long = 0
    var merchantId: Long = 0
    var time: String? = ConstVar.EMPTY_STRING
    var activity: String? = ConstVar.EMPTY_STRING
}