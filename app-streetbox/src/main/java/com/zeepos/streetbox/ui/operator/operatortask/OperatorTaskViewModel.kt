package com.zeepos.streetbox.ui.operator.operatortask


import com.zeepos.domain.interactor.*
import com.zeepos.domain.interactor.parkingsales.GetParkingOperatorTaskUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.models.entities.None

import com.zeepos.models.transaction.TaskOperator
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class OperatorTaskViewModel @Inject constructor(
    private val getParkingOperatorTaskUseCase: GetParkingOperatorTaskUseCase,
    private val shiftOutUseCase: ShiftOutUseCase,
    private val checkUseCase: CheckUseCase,
    private val checkOutUseCase: CheckOutUseCase,
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val getTasksOnGoingUseCase: GetTasksOnGoingUseCase,
    private val checkOutHomeVisitUseCase: CheckOutHomeVisitUseCase
) : BaseViewModel<OperatorTaskViewEvent>() {
    fun getParkingOperatorTask() {
        addDisposable(
            getParkingOperatorTaskUseCase.execute(None())
                .subscribe(
                    {
                        viewEventObservable.postValue(
                            OperatorTaskViewEvent.GetAllParkingOperatorTaskSuccess(it as MutableList<TaskOperator>)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorTaskViewEvent.GetAllParkingOperatorTaskFailed(it)
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
                            OperatorTaskViewEvent.GetShiftOutSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorTaskViewEvent.GetShiftOutFailed(it)
                            )
                        }
                    }
                )
        )
    }

    fun checkOutOperatorTask(latitude:Double, longitude:Double, parkingSpaceName:String, tasksId:Long, typesId: Long) {
        addDisposable(
            checkOutUseCase.execute(CheckOutUseCase.Params(latitude,longitude,parkingSpaceName, tasksId, typesId))
                .subscribe(
                    {
                        viewEventObservable.postValue(
                            OperatorTaskViewEvent.GetCheckOutSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorTaskViewEvent.GetCheckOutFailed(it)
                            )
                        }
                    }
                )
        )
    }


    fun checkOutOperatorTaskHomeVisit(customerName:String, latitude:Double, longitude:Double , tasksId:Long, typesId: Long) {
        addDisposable(
            checkOutHomeVisitUseCase.execute(CheckOutHomeVisitUseCase.Params(customerName, latitude,longitude, tasksId, typesId))
                .subscribe(
                    {
                        viewEventObservable.postValue(
                            OperatorTaskViewEvent.GetCheckOutHomeVisitSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorTaskViewEvent.GetCheckOutHomeVisitFailed(it)
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
                            OperatorTaskViewEvent.GetTaskOnGoingSuccess(it.toString())
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorTaskViewEvent.GetTaskOnGoingFailed(it)
                            )
                        }
                    }
                )
        )
    }



    fun deleteSession(){
        localPreferencesRepository.deleteSession()
    }

}