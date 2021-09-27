package com.zeepos.localstorage

import com.zeepos.domain.repository.ParkingSlotRepo
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.models.entities.Schedule
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.ParkingSlot
import com.zeepos.models.master.ParkingSpace
import com.zeepos.models.master.ParkingSpace_
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 5/15/20
 */
class ParkingSpaceRepositoryImpl @Inject internal constructor(
    private val retrofit: Retrofit,
    boxStore: BoxStore,
    private val parkingSlotRepo: ParkingSlotRepo,
    private val syncDataRepo: SyncDataRepo
) : ParkingSpaceRepository {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    private val box: Box<ParkingSpace> by lazy {
        boxStore.boxFor(
            ParkingSpace::class.java
        )
    }

    override fun insertUpdateParkingSpace(parkingSpaceList: List<ParkingSpace>) {
        box.put(parkingSpaceList)
    }

    override fun getParkingSpace(): Single<List<ParkingSpace>> {
        val dataLocal = box.query().orderDesc(ParkingSpace_.createdAt).build().find()

        if (dataLocal.isNotEmpty())
            return Single.fromCallable { dataLocal }

        return getParkingSpaceCloud(true)
    }

    override fun getParkingSpace(id: Long): ParkingSpace? {
        return box.get(id)
    }

    override fun attach(parkingSpace: ParkingSpace) {
        box.attach(parkingSpace)
    }

    override fun searchParkingSpace(keyword: String): Maybe<List<ParkingSpace>> {
        val results = box.query().contains(ParkingSpace_.name, keyword).build().find()
        if (results.isNotEmpty()) {
            return Maybe.fromCallable { results }
        }

        return service.searchParkingSpace(keyword)
            .map {
                if (it.isSuccess()) {
                    it.data
                }
                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getParkingSpaceCloud(isRefresh: Boolean): Single<List<ParkingSpace>> {

        val queryMap: MutableMap<String, String> = mutableMapOf()
        queryMap["sort"] = "created_at,desc"

        if (isRefresh) {
            queryMap["page"] = "1"
        } else {
            val syncData = syncDataRepo.getByDataType(this::class.java.simpleName)

            if (syncData != null) {
                queryMap["page"] = syncData.nextPage.toString()
            } else {
                queryMap["page"] = "1"
            }
        }

        return service.getParkingSpace(queryMap)
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
                    it.data?.let { data ->
                        if (data.isNotEmpty()) {

                            if (it.page == 1) {
                                box.removeAll()
                                syncDataRepo.remove(this::class.java.simpleName)
                            }

                            insertUpdateParkingSpace(data)

                            syncDataRepo.insertUpdate(
                                ObjectFactory.createSyncData(
                                    this::class.java.simpleName,
                                    it.nextPage
                                )
                            )
                        }
                        return@map data
                    }
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getParkingSchedule(parkingSpaceId: Long): Single<List<ParkingSchedule>> {
        return service.getParkingSpaceSchedule(parkingSpaceId)
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
                    val data = it.data!!
                    return@map data

                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getMerchantParkingSchedule(
        merchantId: Long,
        typesId: Long
    ): Single<List<Schedule>> {
        return service.getMerchantParkingSpaceSchedule(typesId)
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
                    val data = it.data!!
                    return@map data

                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    private fun handleParkingSpaceData(parkingSpaceList: List<ParkingSpace>) {
        val parkingSlotList = arrayListOf<ParkingSlot>()

        parkingSpaceList.forEach {
            parkingSlotList.addAll(it.slot)
            box.attach(it)
            it.parkingSlots.addAll(it.slot)
        }

        parkingSlotRepo.insertUpdate(parkingSlotList)
        insertUpdateParkingSpace(parkingSpaceList)
    }
}