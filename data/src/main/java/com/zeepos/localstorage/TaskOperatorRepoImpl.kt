package com.zeepos.localstorage

import com.zeepos.domain.interactor.*
import com.zeepos.domain.interactor.operator.task.CreateTaskOperatorUseCase
import com.zeepos.domain.repository.ParkingSlotRepo
import com.zeepos.domain.repository.ParkingSlotSalesRepo
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Check
import com.zeepos.models.master.FoodTruckAssign
import com.zeepos.models.master.User
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.models.transaction.TaskOperator_
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 5/22/20
 */
class TaskOperatorRepoImpl @Inject constructor(
    private val parkingSlotRepo: ParkingSlotRepo,
    private val parkingSlotSalesRepo: ParkingSlotSalesRepo,
    private val retrofit: Retrofit,
    boxStore: BoxStore
) : TaskOperatorRepo {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    private val box: Box<TaskOperator> by lazy {
        boxStore.boxFor(
            TaskOperator::class.java
        )
    }

    private val userBox: Box<User> by lazy {
        boxStore.boxFor(
            User::class.java
        )
    }

    override fun createTaskOperator(params: CreateTaskOperatorUseCase.Params): Completable {
        return service.createTaskOperator(
            params.createTask.trxId,
            params.createTask.usersId,
            params.createTask.scheduleDate
        )
            .onErrorResumeNext {
                Completable.error {
                    RetrofitException.handleRetrofitException(
                        it,
                        retrofit
                    )
                }
            }
    }


    override fun createTaskOperatorHomeVisit(params: CreateOperatorHomeVisitUseCase.Params): Completable {
        return service.createTaskOperatorHomeVisit(params
        )
            .onErrorResumeNext {
                Completable.error {
                    RetrofitException.handleRetrofitException(
                        it,
                        retrofit
                    )
                }
            }
    }

    override fun get(id: Long): TaskOperator? {
        return box.get(id)
    }

    override fun getByParkingSlotSalesId(parkingSlotSalesId: Long): TaskOperator? {
        return box.query().equal(TaskOperator_.parkingSlotSalesId, parkingSlotSalesId).build()
            .findFirst()
    }

    override fun getByParkingSalesId(parkingSalesId: Long): List<TaskOperator> {
        return box.query()
            .equal(TaskOperator_.parkingSalesId, parkingSalesId)
            .equal(TaskOperator_.status, ConstVar.TASK_STATUS_OPEN.toLong())
            .build().find()
    }

    override fun getByParkingSalesIdCloud(parkingSalesId: Long): Maybe<List<TaskOperator>> {
        return service.getTaskOperatorByParkingSales(parkingSalesId)
            .map {
                if (it.isSuccess()) {
                    val taskOperatorList = arrayListOf<TaskOperator>()
                    it.data?.forEach { taskOperator ->
                        taskOperator.parkingSalesId = parkingSalesId
                        taskOperatorList.add(taskOperator)
                    }

                    insertUpdate(taskOperatorList)
                    return@map getByParkingSalesId(parkingSalesId)
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getAllOpenTask(): List<TaskOperator> {
        return box.query()
            .equal(TaskOperator_.status, ConstVar.TASK_STATUS_OPEN.toLong())
            .or()
            .equal(TaskOperator_.status, ConstVar.TASK_STATUS_IN_PROGRESS.toLong())
            .build().find()
    }

    override fun insertUpdate(taskOperator: TaskOperator) {
        box.put(taskOperator)
    }

    override fun insertUpdate(taskOperatorList: List<TaskOperator>) {
        box.put(taskOperatorList)
    }

    override fun getMyTask(): Single<List<TaskOperator>> {
        return service.getOperatorTask()
            .map {
                if (it.isSuccess()) {
                    val taskOperatorList = it.data
//                    if (taskOperatorList != null)
//                        insertUpdate(taskOperatorList)
                    return@map taskOperatorList
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getFreeTask(): Single<ResponseApi<TaskOperator>> {
        return service.getOperatorFreeTask()
    }


    override fun undo(tasksId: Long): Single<ResponseApi<String>> {
        return service.undo(tasksId)
    }

    override fun taksOnGoing(tasksId: Long): Single<ResponseApi<String>> {
        return service.taksOnGoing(tasksId)
    }

    override fun createFreeTask(param: None): Single<ResponseApi<Check>> {
        return service.createFreeTask(param)
    }

    override fun clearAll() {
        box.removeAll()
    }

    override fun checkIn(param: CheckUseCase.Params): Single<ResponseApi<Check>> {
        return service.checkin(param)
    }

    override fun checkOut(param: CheckOutUseCase.Params): Single<ResponseApi<Check>> {
        return service.checkout(param)
    }

    override fun checkInHomeVisit(param: CheckInHomeVisitUseCase.Params): Single<ResponseApi<Check>> {
        return service.checkinHomeVisit(param)
    }

    override fun checkOutHomeVisit(param: CheckOutHomeVisitUseCase.Params): Single<ResponseApi<Check>> {
        return service.checkoutHomeVisit(param)
    }


    override fun checkInFreeTask(param: CheckFreeTaskUseCase.Params): Single<ResponseApi<Check>> {
        return service.checkinFreeTask(param)
    }

    override fun checkOutFreeTask(param: CheckOutFreeTaskUseCase.Params): Single<ResponseApi<Check>> {
        return service.checkoutFreeTask(param)
    }

}