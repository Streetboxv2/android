package com.zeepos.localstorage

import com.zeepos.domain.repository.OrderRepo
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.OrderHistory
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.Order_
import com.zeepos.models.transaction.TaxSales
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import com.zeepos.utilities.DateTimeUtil
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 5/17/20
 */
class OrderRepoImpl @Inject internal constructor(
    boxStore: BoxStore,
    private val retrofit: Retrofit,
    private val userRepository: UserRepository
) : OrderRepo {
    private val box: Box<Order> = boxStore.boxFor(
        Order::class.java
    )

    private val boxUser: Box<User> = boxStore.boxFor(
        User::class.java
    )

    private val boxTaxSales: Box<TaxSales> = boxStore.boxFor(
        TaxSales::class.java
    )

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }


    override fun removeAll() {
        box.removeAll()
        boxUser.removeAll()
        boxTaxSales.removeAll()
    }

    override fun getRecentOpenOrCreateOrder(
        merchantId: Long,
        foodTruck: FoodTruck?
    ): Single<Order> {
        return Single.fromCallable {
            var order: Order? = if (merchantId > 0) { //end user
                box.query().equal(Order_.isClose, false)
                    .equal(Order_.types, 1)
                    .equal(Order_.businessDate, DateTimeUtil.getCurrentDateWithoutTime())
                    .orderDesc(Order_.createdAt).build()
                    .findFirst()
            } else {
                box.query().equal(Order_.isClose, false)
                    .equal(Order_.types, 0)
                    .equal(Order_.businessDate, DateTimeUtil.getCurrentDateWithoutTime())
                    .orderDesc(Order_.createdAt).build()
                    .findFirst()
            }

//            if (order == null) {
                var orderNo:String = ConstVar.EMPTY_STRING
                if(merchantId > 0){
                    orderNo =
                        getCountOrderUser(DateTimeUtil.getCurrentDateWithoutTime())?.toInt()
                            ?.inc()?.toString() ?: "1"
                }else{
                    orderNo =
                        getCountOrder(DateTimeUtil.getCurrentDateWithoutTime())?.toInt()
                            ?.inc()?.toString() ?: "1"
                }

                val userMerChant = userRepository.getProfileMerchant()
                val user = userRepository.getCurrentUser()


                if (user != null) {
                    val newOrder = ObjectFactory.createOrder(
                        merchantId,
                        user, orderNo, foodTruck, userMerChant
                    )

                    box.put(newOrder)

                    order = getOrder(newOrder.uniqueId)
                }
//            }

            if (order != null)
                return@fromCallable order
            throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
        }
    }

    override fun getLastOpenedOrder(): Order? {
        return box.query().equal(Order_.isClose, false).orderDesc(Order_.createdAt).build()
            .findFirst()
    }

    override fun getCart(): Single<Order> {
        return Single.fromCallable {
            val order = box.query().equal(Order_.isClose, false).orderDesc(Order_.createdAt).build()
                .findFirst()

            if (order != null)
                return@fromCallable order
            throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))

        }
    }

    override fun getLastClosedOrder(businessDate: Long): Order? {
        return box.query()
            .equal(Order_.isClose, true)
            .equal(Order_.businessDate, businessDate)
            .equal(Order_.types, 0)
            .orderDesc(Order_.createdAt).build()
            .findFirst()
    }

    override fun getCountOrder(businessDate: Long): Long? {
        return box.query()
            .equal(Order_.isClose, true)
            .equal(Order_.businessDate, businessDate)
            .equal(Order_.types, 0)
            .orderDesc(Order_.createdAt).build()
            .count()
    }

    override fun getCountOrderUser(businessDate: Long): Long? {
        return box.query()
            .equal(Order_.isClose, true)
            .equal(Order_.businessDate, businessDate)
            .equal(Order_.types, 1)
            .orderDesc(Order_.createdAt).build()
            .count()
    }

    override fun getOrder(id: Long): Order? {
        return box.get(id)
    }

    override fun getOrder(uniqueId: String): Order? {
        return box.query().equal(Order_.uniqueId, uniqueId).build().findFirst()
    }

    override fun getOrderCloud(page: Int, filter: String): Single<List<OrderHistory>> {
        val queryMap: MutableMap<String, String> = mutableMapOf()
        queryMap["page"] = page.toString()
        queryMap["limit"] = "10"
        queryMap["filter"] = filter

        return service.getOrderHistoryEndUser(queryMap)
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(it, retrofit)
                }
            }
            .map {
                if (it.isSuccess()) {
                    val data = it.data!!
                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun closeOrder(uniqueId: String): Completable {
        return Completable.fromCallable {
            val order = getOrder(uniqueId)
            if (order != null) {
                order.isClose = true
                order.updatedAt = DateTimeUtil.getCurrentDateTime()
                insertUpdate(order)
            }
        }
    }

    override fun insertUpdate(order: Order) {
        box.put(order)
    }

    override fun update(order: Order): Completable {
        return Completable.fromCallable { box.put(order) }
    }
}