package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 7/31/20
 */
class OrderHistory {
    var trxId: String? = ConstVar.EMPTY_STRING
    var logo: String? = ConstVar.EMPTY_STRING
    var merchantName: String? = ConstVar.EMPTY_STRING
    var date: String? = ConstVar.EMPTY_STRING
    var status: String? = ConstVar.EMPTY_STRING
    var amount: Double = 0.0
    var types: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var qrCode: String? = ConstVar.EMPTY_STRING
    var phone: String? = ConstVar.EMPTY_STRING
    var notes: String? = ConstVar.EMPTY_STRING
    var detail: OrderHistoryDetail? = null
}