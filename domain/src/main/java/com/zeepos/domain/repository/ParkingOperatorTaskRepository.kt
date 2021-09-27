package com.zeepos.domain.repository

import com.zeepos.models.master.OperatorTask
import com.zeepos.models.transaction.ParkingSales
import io.reactivex.Single

interface ParkingOperatorTaskRepository {
    fun getOperatorTask(): List<OperatorTask>
    fun insertUpdateParkingOperator(parkingOperatorList: List<OperatorTask>)
    fun getOperatorTaskCloud(): Single<List<OperatorTask>>
}