package com.zeepos.domain.repository

import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import io.reactivex.Single

/**
 * Created by Arif S. on 7/3/20
 */
interface OrderBillRepo {
    fun calculateOrder(order: Order): Single<OrderBill>
    fun getByOrderUniqueId(orderUniqueId: String): OrderBill?
    fun getAllOrderBill(): Single<List<OrderBill>>
    fun getAllOrderBillDesc(): OrderBill?
    fun insertUpdateOrderBill(orderBill: OrderBill)
    fun insertUpdateOrderBill(orderBill: List<OrderBill>)
}