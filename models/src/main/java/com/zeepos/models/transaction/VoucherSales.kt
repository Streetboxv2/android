package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 5/17/20
 */
@Entity
data class VoucherSales(
    @Id var id: Long = 0,
    var uniqueId: String,
    var orderUniqueId: String
) {
    var name: String = ConstVar.EMPTY_STRING
    var code: String = ConstVar.EMPTY_STRING
    var amount: Double = 0.0
    var createdAt: Long = 0L
    var updatedAt: Long = 0L

    lateinit var order: ToOne<Order>

    lateinit var orderBill: ToOne<OrderBill>

}