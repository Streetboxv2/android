package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import com.zeepos.utilities.gson.Exclude
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 5/17/20
 */
@Entity
class PaymentSales {
    @Id
    var id: Long = 0
    var uniqueId: String = ConstVar.EMPTY_STRING
    var orderUniqueId: String = ConstVar.EMPTY_STRING
    var orderBillUniqueId: String = ConstVar.EMPTY_STRING
    var no = 0
    var name: String = ConstVar.EMPTY_STRING
    var type: Int = 0
    var amount: Double = 0.0
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var paymentMethodId: Long = 0L
    var trxId: String? = ConstVar.EMPTY_STRING
    var status: String? = ConstVar.EMPTY_STRING
    var qrCode: String? = ConstVar.EMPTY_STRING

    @Exclude
    lateinit var order: ToOne<Order>

    @Exclude
    lateinit var orderBill: ToOne<OrderBill>
}