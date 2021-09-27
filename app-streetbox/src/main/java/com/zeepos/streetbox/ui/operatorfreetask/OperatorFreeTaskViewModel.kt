package com.zeepos.streetbox.ui.operatorfreetask

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.zeepos.domain.interactor.*
import com.zeepos.domain.interactor.map.UpdateCurrentLocationToCloud
import com.zeepos.map.ui.MapViewEvent
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class OperatorFreeTaskViewModel @Inject constructor(
    private val checkOutFreeTaskSuccess: CheckOutFreeTaskUseCase,
    private val getListFreeTaskUseCase: GetListFreeTaskUseCase,
    private val getStatusNonRegulerUseCase: GetStatusNonRegulerUseCase,
    private val getTasksOnGoingUseCase: GetTasksOnGoingUseCase,
    private val createFreeTaskUseCase: CreateFreeTaskUseCase,
    private val shiftOutUseCase: ShiftOutUseCase,
    private val updateCurrentLocationToCloud: UpdateCurrentLocationToCloud,
    private val checkFreeTaskUseCase: CheckFreeTaskUseCase,
    private val checkUseCase: CheckUseCase
) : BaseViewModel<OperatorFreeTaskViewEvent>() {

    val location: MutableLiveData<Location> = MutableLiveData()

    fun checkOutOperatorFreeTask(address: String, latitude: Double, longitude: Double, tasksId:Long) {
        addDisposable(
            checkOutFreeTaskSuccess.execute( CheckOutFreeTaskUseCase.Params(address, latitude, longitude, tasksId))
                .subscribe(
                    {
                        viewEventObservable.postValue(
                            OperatorFreeTaskViewEvent.GetCheckOutFreeTaskSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetCheckOutFreeTaskFailed(it)
                            )
                        }
                    }
                )
        )
    }


    fun getStatusNonRegulerTask() {
        addDisposable(
            getStatusNonRegulerUseCase.execute(None())
                .subscribe(

                    {
                        if(it.data!!.equals("")){
                            Log.d("tes","tes")
                        }
                        viewEventObservable.postValue(
                            OperatorFreeTaskViewEvent.GetStatusNonRegulerSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetStatusNonRegulerFailed(it)
                            )
                        }
                    }
                )
        )
    }

    fun getParkingOperatorFreeTask() {
        addDisposable(
            getListFreeTaskUseCase.execute(None())
                .subscribe(
                    {
                        if(it.data == null){
                          val data =  TaskOperator()
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetAllParkingOperatorFreeTaskSuccess(data)
                            )
                        }else {
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetAllParkingOperatorFreeTaskSuccess(it.data!!)
                            )
                        }
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetAllParkingOperatorFreeTaskFailed(it)
                            )
                        }
                    }
                )
        )
    }


    fun createOperatorFreeTask() {
        addDisposable(
            createFreeTaskUseCase.execute(None())
                .subscribe(
                    {
                        viewEventObservable.postValue(
                            OperatorFreeTaskViewEvent.GetCreateFreeTaskSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetCreateFreeTaskFailed(it)
                            )
                        }
                    }
                )
        )
    }

    fun taskOnGoing(tasksId: Long){
        addDisposable(
            getTasksOnGoingUseCase.execute(GetTasksOnGoingUseCase.Params(tasksId))
                .subscribe(
                    {
                        viewEventObservable.postValue(
                            OperatorFreeTaskViewEvent.GetTaskOnGoingSuccess(it.toString())
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetTaskOnGoingFailed(it)
                            )
                        }
                    }
                )
        )
    }


    fun shiftOutOperatorTask() {
        addDisposable(
            shiftOutUseCase.execute(None())
                .subscribe(

                    {
                        viewEventObservable.postValue(
                            OperatorFreeTaskViewEvent.GetShiftOutSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetShiftOutFailed(it)
                            )
                        }
                    }
                )
        )
    }

    fun checkInOperatorFreeTask(
        address: String,
        latitude: Double,
        longitude: Double,
        tasksId: Long
    ) {
        addDisposable(
            checkFreeTaskUseCase.execute(
                CheckFreeTaskUseCase.Params(
                    address,
                    latitude,
                    longitude,
                    tasksId
                )
            )
                .subscribe(
                    {
                        updateLocationToCloud(
                            tasksId,
                            latitude,
                            longitude,
                            ConstVar.TASK_STATUS_ARRIVED
                        )
                        viewEventObservable.postValue(
                            OperatorFreeTaskViewEvent.GetCheckInFreeTaskSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFreeTaskViewEvent.GetCheckInFreeTaskFailed(it)
                            )
                        }
                    }
                )
        )
    }

    fun updateLocationToCloud(taskId: Long, latitude: Double, longitude: Double, status: Int = 0) {
        val disposable = updateCurrentLocationToCloud.execute(
            UpdateCurrentLocationToCloud.Params(
                taskId,
                latitude,
                longitude,
                status
            )
        )
            .subscribe({
                Log.d(ConstVar.TAG, "Update status success!")
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }
}



