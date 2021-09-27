package com.zeepos.localstorage

import com.zeepos.domain.interactor.parkingsales.CreateOrUpdateParkingSalesUseCase
import com.zeepos.domain.repository.*
import com.zeepos.models.ConstVar
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.User
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.models.transaction.ParkingSales_
import com.zeepos.models.transaction.ParkingSlotSales
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import com.zeepos.utilities.DateTimeUtil
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 5/17/20
 */
class ParkingSalesRepoImpl @Inject constructor(
    boxStore: BoxStore,
    private val retrofit: Retrofit,
    private val userRepository: UserRepository,
    private val parkingSlotSalesRepo: ParkingSlotSalesRepo,
    private val orderRepo: OrderRepo,
    private val syncDataRepo: SyncDataRepo
) : ParkingSalesRepo {

    private val box: Box<ParkingSales> by lazy {
        boxStore.boxFor(
            ParkingSales::class.java
        )
    }

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    override fun getParkingSales(): Single<List<ParkingSales>> {
        val dataLocal = box.query().orderDesc(ParkingSales_.createdAt).build().find()

        if (dataLocal.isNotEmpty())
            return Single.fromCallable { dataLocal }

        return getParkingSalesCloud(true)
    }

    override fun getByParkingSpaceId(parkingSpaceId: Long): ParkingSales? {
        return box.query().equal(ParkingSales_.parkingSpaceId, parkingSpaceId).build().findFirst()
    }

    override fun getParkingSalesDetail(id: Long): Single<ParkingSales> {

        return service.getParkingSalesSlot(id)
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(
                        it,
                        retrofit
                    )
                }
            }
            .map {
                if (it.isSuccess()) {
                    val parkingSales = box.get(id)
                    val parkingSlotSalesList = arrayListOf<ParkingSlotSales>()
                    it.data?.forEach { parkingSlotSales ->
                        parkingSlotSales.parkingSales.target = parkingSales
                        parkingSlotSalesList.add(parkingSlotSales)
                    }

                    parkingSlotSalesRepo.insertUpdate(parkingSlotSalesList)
                    return@map box.get(parkingSales.id)
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getByParkingSpaceIdOrderUniqueId(
        parkingSpaceId: Long,
        orderUniqueId: String
    ): ParkingSales? {
        val builder = box.query()
            .equal(ParkingSales_.parkingSpaceId, parkingSpaceId)
            .equal(ParkingSales_.orderUniqueId, orderUniqueId)
        builder.link(ParkingSales_.parkingSlotSales)

        return builder.build().findFirst()
    }

    override fun insertUpdate(parkingSales: ParkingSales) {
        box.put(parkingSales)
    }

    override fun insertUpdate(parkingSalesList: List<ParkingSales>) {
        box.put(parkingSalesList)
    }

    override fun createOrUpdateParkingSales(params: CreateOrUpdateParkingSalesUseCase.Params) {
        var order = orderRepo.getLastOpenedOrder()
        var isCreateNewOrder = false

        if (order == null) {
            isCreateNewOrder = true
            val orderNo = orderRepo.getLastClosedOrder(DateTimeUtil.getCurrentDateWithoutTime())?.orderNo?.toInt()?.inc()?.toString() ?: "1"
            val user: User = userRepository.getCurrentUser()!!

            order = ObjectFactory.createOrder(user.id, user, orderNo)
            order.user.target = user
        }

        var parkingSales = getByParkingSpaceIdOrderUniqueId(params.parkingSpace.id, order.uniqueId)

        if (parkingSales == null) {
            parkingSales =
                ObjectFactory.createParkingSales(order, params.parkingSpace, params.parkingSlot)
        }

        val parkingSlotSales = ObjectFactory.createParkingSlotSales(
            parkingSales,
            params.parkingSlot
        )

        parkingSales.parkingSlotSales.add(parkingSlotSales)
        order.parkingSales.add(parkingSales)

        if (isCreateNewOrder) {
            orderRepo.insertUpdate(order)
        } else {
            box.put(parkingSales)
        }
    }

    override fun getParkingSalesCloud(isRefresh: Boolean): Single<List<ParkingSales>> {

        val queryMap: MutableMap<String, String> = mutableMapOf()

        if (isRefresh) {
            queryMap["page"] = "1"
        } else {
            val syncData = syncDataRepo.getByDataType(ParkingSales::class.java.simpleName)

            if (syncData != null) {
                queryMap["page"] = syncData.nextPage.toString()
            } else {
                queryMap["page"] = "1"
            }
        }

        return service.getParkingSales(queryMap)
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(
                        it,
                        retrofit
                    )
                }
            }
            .map {
                if (it.isSuccess()) {
                    it.data?.let { list ->
                        if (list.isNotEmpty()) {

//                            if (it.page == 1) {//no pagination
                            box.removeAll()
                            syncDataRepo.removeAll()
//                            }

                            insertUpdate(list)

                            syncDataRepo.insertUpdate(
                                ObjectFactory.createSyncData(
                                    ParkingSales::class.java.simpleName,
                                    it.nextPage
                                )
                            )
                        }
                        return@map list
                    }
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun searchParkingSales(keyword: String): Maybe<List<ParkingSales>> {
        val results = box.query().contains(ParkingSales_.name, keyword).build().find()
        if (results.isNotEmpty()) {
            return Maybe.fromCallable { results }
        }

        return service.searchParkingSpaceSales(keyword)
            .map {
                if (it.isSuccess()) {
                    it.data
                }
                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

}