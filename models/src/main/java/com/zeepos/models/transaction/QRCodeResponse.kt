package com.zeepos.models.transaction

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 7/28/20
 */
class QRCodeResponse {
    var amount: Double = 0.0
    var createdAt: String? = ConstVar.EMPTY_STRING
    var id: String? = ConstVar.EMPTY_STRING
    var qrCode: String? = ConstVar.EMPTY_STRING
    var status: String? = ConstVar.EMPTY_STRING
    var trxId: String? = ConstVar.EMPTY_STRING
    var type: String? = ConstVar.EMPTY_STRING
    var updatedAt: String? = ConstVar.EMPTY_STRING
    var callbackUrl: String? = ConstVar.EMPTY_STRING
}