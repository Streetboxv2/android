package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 7/29/20
 */
class SyncResponse {
    var businessDate: String? = ConstVar.EMPTY_STRING
    var data: String? = ConstVar.EMPTY_STRING
    var id: Long = 0
    var merchantId: Long = 0
    var status: Int = 0
    var syncDate: String? = ConstVar.EMPTY_STRING
    var uniqueId: String? = ConstVar.EMPTY_STRING
}