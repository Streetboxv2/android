package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import com.zeepos.utilities.gson.Exclude
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 7/3/20
 */
@Entity
class OrderBill {
    @Id
    var id: Long = 0
    var uniqueId: String = ConstVar.EMPTY_STRING
    var orderUniqueId: String = ConstVar.EMPTY_STRING
    var billNo: String = ConstVar.EMPTY_STRING
    var isClose: Boolean = true
    var totalDiscount: Double = 0.0
    var subTotal: Double = 0.0
    var totalTax: Double = 0.0
    var taxType: Int = ConstVar.TAX_TYPE_EXCLUSIVE
    var taxName: String = ConstVar.EMPTY_STRING
    var grandTotal: Double = 0.0
    var businessDate: Long = 0L
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var type: Int = 0
    var merchantId: Long = 0

    @Exclude
    lateinit var order: ToOne<Order>

    @Exclude
    @Backlink(to = "orderBill")
    lateinit var productSales: ToMany<ProductSales>

    @Exclude
    @Backlink(to = "orderBill")
    lateinit var paymentSales: ToMany<PaymentSales>

    @Exclude
    @Backlink(to = "orderBill")
    lateinit var vouchersSales: ToMany<VoucherSales>
}