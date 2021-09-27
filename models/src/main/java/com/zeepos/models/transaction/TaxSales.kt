package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import com.zeepos.utilities.gson.Exclude
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
class TaxSales {
    @Id
    var id: Long = 0
    var amount: Double = 0.0
    var orderBillUniqueId: String = ConstVar.EMPTY_STRING
    var orderUniqueId: String = ConstVar.EMPTY_STRING
    var type: Int = 0
    var name: String = ConstVar.EMPTY_STRING
    var uniqueId: String = ConstVar.EMPTY_STRING
    var updatedAt: Long = 0
    var createdAt: Long = 0
    var merchantTaxId: Long = 0
    var isActive: Boolean = true
    var merchantId: Long = 0

    @Exclude
    lateinit var order: ToOne<Order>

    @Exclude
    lateinit var orderBill: ToOne<OrderBill>

}