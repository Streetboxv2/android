package com.zeepos.models.entities

/**
 * Created by Arif S. on 8/11/20
 */
class PaymentDetail {
    var total: Double = 0.0
    var tax: Double = 0.0
    var taxName: String? = "Tax"
    var taxType: Int = 0
    var subtotal: Double = 0.0
    var isActive:Boolean = false
}