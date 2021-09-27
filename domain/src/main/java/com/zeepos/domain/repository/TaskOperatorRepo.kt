package com.zeepos.domain.repository

import com.zeepos.domain.interactor.*
import com.zeepos.domain.interactor.operator.task.CreateTaskOperatorUseCase
import com.zeepos.models.entities.CreateTask
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Check
import com.zeepos.models.transaction.TaskOperator
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by Arif S. on 5/22/20
 */
interface TaskOperatorRepo {
    fun createTaskOperator(params: CreateTaskOperatorUseCase.Params): Completable
    fun createTaskOperatorHomeVisit(params: CreateOperatorHomeVisitUseCase.Params): Completable
    fun get(id: Long): TaskOperator?
    fun getByParkingSlotSalesId(parkingSlotSalesId: Long): TaskOperator?
    fun getByParkingSalesId(parkingSalesId: Long): List<TaskOperator>
    fun getByParkingSalesIdCloud(parkingSalesId: Long): Maybe<List<TaskOperator>>
    fun getAllOpenTask(): List<TaskOperator>
    fun insertUpdate(taskOperator: TaskOperator)
    fun insertUpdate(taskOperatorList: List<TaskOperator>)
    fun getMyTask(): Single<List<TaskOperator>>
    fun checkIn(param: CheckUseCase.Params): Single<ResponseApi<Check>>
    fun checkOut(param: CheckOutUseCase.Params): Single<ResponseApi<Check>>
    fun checkInHomeVisit(param: CheckInHomeVisitUseCase.Params): Single<ResponseApi<Check>>
    fun checkOutHomeVisit(param: CheckOutHomeVisitUseCase.Params): Single<ResponseApi<Check>>
    fun checkInFreeTask(param: CheckFreeTaskUseCase.Params): Single<ResponseApi<Check>>
    fun checkOutFreeTask(param: CheckOutFreeTaskUseCase.Params): Single<ResponseApi<Check>>
    fun getFreeTask(): Single<ResponseApi<TaskOperator>>
    fun undo(tasksId: Long): Single<ResponseApi<String>>
    fun taksOnGoing(tasksId: Long): Single<ResponseApi<String>>
    fun createFreeTask(param: None): Single<ResponseApi<Check>>
    fun clearAll()
}