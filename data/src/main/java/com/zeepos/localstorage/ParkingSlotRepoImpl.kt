package com.zeepos.localstorage

import com.zeepos.domain.repository.ParkingSlotRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.master.ParkingSlot
import com.zeepos.models.master.ParkingSlot_
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
 * Created by Arif S. on 5/18/20
 */
class ParkingSlotRepoImpl @Inject constructor(
    private val retrofit: Retrofit,
    boxStore: BoxStore
) : ParkingSlotRepo {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    private val box: Box<ParkingSlot> = boxStore.boxFor(
        ParkingSlot::class.java
    )

    override fun insertUpdate(data: ParkingSlot) {
        box.put(data)
    }

    override fun insertUpdate(data: List<ParkingSlot>) {
        box.put(data)
    }

    override fun getByParkingSpaceId(parkingSpaceId: Long): List<ParkingSlot> {
        return box.query().equal(ParkingSlot_.parkingSpaceId, parkingSpaceId).build()
            .find()
    }

    override fun get(id: Long): ParkingSlot? {
        return box.get(id)
    }

    override fun getWithObs(id: Long): Maybe<ParkingSlot> {
        return Maybe.fromAction { box.get(id) }
    }

    override fun getParkingDetail(parkingSpaceId: Long): Single<List<ParkingSlot>> {
        return service.getParkingSlot(parkingSpaceId)
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
                        return@map data
                    }
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }
}