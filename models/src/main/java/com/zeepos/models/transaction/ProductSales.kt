package com.zeepos.models.transaction

import com.zeepos.models.ConstVar
import com.zeepos.utilities.gson.Exclude
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.io.Serializable

/**
 * Created by Arif S. on 7/2/20
 */
@Entity
class ProductSales : Serializable {
    @Id
    var id: Long = 0
    var no: Int = 0
    var uniqueId: String = ConstVar.EMPTY_STRING
    var orderUniqueId: String = ConstVar.EMPTY_STRING
    var orderBillUniqueId: String = ConstVar.EMPTY_STRING
    var productId: Long = 0
    var priceAfterDiscount: Double = 0.0
    var priceOriginal: Double = 0.0
    var discount: Double = 0.0
    var name: String = ConstVar.EMPTY_STRING
    var sku: String = ConstVar.EMPTY_STRING
    var price: Double = 0.0
    var qty: Int = 0
    var qtyProduct: Int = 0
    var type: Int = 0
    var salesTypeId: Int = 0
    var businessDate: Long = 0
    var createDate: Long = 0
    var updateDate: Long = 0
    var subtotal: Double = 0.0
    var createdAt: Long = 0
    var updatedAt: Long = 0
    var notes: String = ConstVar.EMPTY_STRING
    var photo: String = ConstVar.EMPTY_STRING
    var qrCode: String = ConstVar.EMPTY_STRING


    @Exclude
    lateinit var order: ToOne<Order>

    @Exclude
    lateinit var orderBill: ToOne<OrderBill>
}