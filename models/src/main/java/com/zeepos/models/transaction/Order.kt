package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import com.zeepos.utilities.gson.Exclude
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 5/17/20
 */
@Entity
class Order {
    @Id
    var id: Long = 0
    var uniqueId: String = ConstVar.EMPTY_STRING
    var userId: Long = 0
    var merchantId: Long = 0
    var orderNo: String = "0"
    var billNo: String = ConstVar.EMPTY_STRING
    var isClose: Boolean = false
    var businessDate: Long = 0L
    var totalDiscount: Double = 0.0
    var grandTotal: Double = 0.0
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var no: Int = 0
    var note: String = ConstVar.EMPTY_STRING
    var types: Long = 0
    var orderType: String? = ConstVar.EMPTY_STRING
    var trxId: String = ConstVar.EMPTY_STRING
    var merchantUsersId: Long = 0
    var titleToko: String? = ConstVar.EMPTY_STRING//backend request to include address
    var address: String? = ConstVar.EMPTY_STRING//backend request to include address
    var paymentMethodId: String = ConstVar.EMPTY_STRING
    var paymentMethodName: String = ConstVar.EMPTY_STRING
    var typeOrder: String = "Dine In"
    var typePayment: String = ConstVar.EMPTY_STRING
    var status:String = ConstVar.EMPTY_STRING
    var phone:String = ConstVar.EMPTY_STRING

    @Exclude
    lateinit var user: ToOne<User>

    @Exclude
    @Backlink(to = "order")
    lateinit var trx: ToMany<Trx>

    @Exclude
    @Backlink(to = "order")
    lateinit var productSales: ToMany<ProductSales>

    @Exclude
    @Backlink(to = "order")
    lateinit var parkingSales: ToMany<ParkingSales>

    @Exclude
    @Backlink(to = "order")
    lateinit var orderBill: ToMany<OrderBill>

    @Exclude
    @Backlink(to = "order")
    lateinit var paymentSales: ToMany<PaymentSales>

    @Exclude
    @Backlink(to = "order")
    lateinit var vouchersSales: ToMany<VoucherSales>

    @Exclude
    @Backlink(to = "order")
    lateinit var taxSales: ToMany<TaxSales>

}