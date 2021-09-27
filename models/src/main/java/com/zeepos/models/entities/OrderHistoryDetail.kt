package com.zeepos.models.entities

/**
 * Created by Arif S. on 8/11/20
 */
class OrderHistoryDetail {
    var orderDetails: List<OrderDetail> = arrayListOf()
    var paymentDetails: PaymentDetail? = null
    var paymentName: String? = ""
}