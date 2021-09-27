package com.zeepos.localstorage

import android.util.Log
import com.zeepos.domain.repository.ParkingOperatorTaskRepository
import com.zeepos.domain.repository.ParkingSlotRepo
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.*
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.remotestorage.RemoteService
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

class ParkingOperatorTaskImpl @Inject internal constructor(
    retrofit: Retrofit,
    boxStore: BoxStore
) : ParkingOperatorTaskRepository {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    private val box: Box<OperatorTask> by lazy {
        boxStore.boxFor(
            OperatorTask::class.java
        )
    }

    override fun insertUpdateParkingOperator(parkingOperatorList: List<OperatorTask>) {
        box.put(parkingOperatorList)
    }

    override fun getOperatorTask(): List<OperatorTask> {
        return box.query().orderDesc(OperatorTask_.startDate).build().find()
    }

    override fun getOperatorTaskCloud(): Single<List<OperatorTask>> {

//        val dataLocal = getOperatorTask()
//
//        if (dataLocal.isNotEmpty()) {
//            return Single.fromCallable { dataLocal }
//        }
//            return service.getOperatorTask()
//                .map {
//                    if (it.isSuccess()) {
//                        val parkingOperatorList = it.data
//                        if (parkingOperatorList != null)
//                            insertUpdateParkingOperator(parkingOperatorList)
//                        return@map parkingOperatorList
//                    }

                    throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
//                }
    }
}