package com.zeepos.models.entities

import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 8/3/20
 */
class BookHomeVisit {
    var uniqueId: String = ConstVar.EMPTY_STRING
    var address: String = ConstVar.EMPTY_STRING
    var customerName: String = ConstVar.EMPTY_STRING
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var notes: String = ConstVar.EMPTY_STRING
    var grandTotal: Long = 0
    var trxId: String = ConstVar.EMPTY_STRING
    var paymentMethodId: Long = 0
    var phone: String = ConstVar.EMPTY_STRING
    var visitSales: MutableList<VisitSales> = mutableListOf()
}