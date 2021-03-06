package com.zeepos.domain.repository

import com.zeepos.models.entities.OrderHistory
import com.zeepos.models.entities.OrderHistoryDetail
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.transaction.AllTransaction
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Arif S. on 5/17/20
 */
interface OrderRepo {
    fun getRecentOpenOrCreateOrder(merchantId: Long, foodTruck: FoodTruck?): Single<Order>
    fun getLastOpenedOrder(): Order?
    fun getCart(): Single<Order>
    fun getLastClosedOrder(businessDate: Long): Order?
    fun getCountOrder(businessDate: Long): Long?
    fun getCountOrderUser(businessDate: Long): Long?
    fun removeAll()
    fun getOrder(id: Long): Order?
    fun getOrder(uniqueId: String): Order?
    fun getOrderCloudPOS(trxId: String): Single<AllTransaction>
    fun getOrderCloud(page: Int, filter: String): Single<List<OrderHistory>>
    fun getOrderCloudId(trxId:String):Single<OrderHistoryDetail>
    fun closeOrder(uniqueId: String): Completable
    fun insertUpdate(order: Order)
    fun update(order: Order): Completable
}