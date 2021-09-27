package com.zeepos.streetbox.ui.operatormerchant

import androidx.lifecycle.MutableLiveData
import com.zeepos.domain.interactor.CreateOperatorHomeVisitUseCase
import com.zeepos.domain.interactor.operator.GetAllAvailableOperatorUseCase
import com.zeepos.domain.interactor.operator.GetAllOperatorUseCase
import com.zeepos.domain.interactor.operator.SearchOperatorUseCase
import com.zeepos.domain.interactor.operator.task.CreateTaskOperatorUseCase
import com.zeepos.domain.interactor.user.GetUserUseCase
import com.zeepos.domain.repository.ParkingSlotSalesRepo
import com.zeepos.models.entities.CreateHomeVisitTask
import com.zeepos.models.entities.CreateTask
import com.zeepos.models.entities.None
import com.zeepos.models.master.User
import com.zeepos.models.transaction.ParkingSlotSales
import com.zeepos.ui_base.ui.BaseViewModel
import com.zeepos.utilities.DateTimeUtil
import io.reactivex.ObservableSource
import javax.inject.Inject

class OperatorViewModel @Inject constructor(
    private val getAllAvailableOperatorUseCase: GetAllAvailableOperatorUseCase,
    private val getAllOperatorUseCase: GetAllOperatorUseCase,
    private val createOperatorUseCase: CreateTaskOperatorUseCase,
    private val searchOperatorUseCase: SearchOperatorUseCase,
    private val parkingSlotSalesRepo: ParkingSlotSalesRepo,
    private val createOperatorHomeVisitUseCase: CreateOperatorHomeVisitUseCase,
    private val getUserUseCase: GetUserUseCase
) : BaseViewModel<OperatorViewEvent>() {

    var foodTruckObservable: MutableLiveData<User> = MutableLiveData()

    fun getAvailableListOperators() {
        val operatorDisposable =
            getAllAvailableOperatorUseCase.execute(None())
                .subscribe({
                    handleCallback(OperatorViewEvent.GetOperatorSuccess(it))
                }, {
                    handleCallback(OperatorViewEvent.GetOperatorFailed(it.message))
                })

        addDisposable(operatorDisposable)
    }

    fun getAllOperator() {
        val disposable = getAllOperatorUseCase.execute(None())
            .subscribe({
                handleCallback(OperatorViewEvent.GetOperatorSuccess(it))
            }, {
                handleCallback(OperatorViewEvent.GetOperatorFailed(it.message))
            })
        addDisposable(disposable)
    }

    fun getFoodTruck(id: Long) {
        val disposable = getUserUseCase.execute(GetUserUseCase.Params(id))
            .subscribe({
                if (it.isSuccess()) {
                    val foodTruck = it.data!!
                    foodTruckObservable.postValue(foodTruck)
                }
            }, { it.printStackTrace() })
        addDisposable(disposable)
    }

    fun getParkingSlotSales(parkingSlotSalesId: Long): ParkingSlotSales? {
        return parkingSlotSalesRepo.get(parkingSlotSalesId)
    }

    fun getParkingSlotAvailableDate(parkingSlotSalesId: Long, foodTruckId: Long): List<Long> {
        val activeDate: MutableList<Long> = mutableListOf()
        val parkingSlotSales = getParkingSlotSales(parkingSlotSalesId)
        if (parkingSlotSales != null) {
            val startDateMillis =
                DateTimeUtil.getDateFromString(parkingSlotSales.startDate)?.time ?: 0L
            val endDateMillis = DateTimeUtil.getDateFromString(parkingSlotSales.endDate)?.time ?: 0L
            val endDateWithoutTime = DateTimeUtil.getCurrentDateWithoutTime(endDateMillis)
            var nextDate = DateTimeUtil.getCurrentDateWithoutTime(startDateMillis)

            do {
                val currentDate = nextDate

                activeDate.add(currentDate)
                nextDate = DateTimeUtil.getNextDate(currentDate)
            } while (currentDate != endDateWithoutTime)
        }

        return activeDate
    }

    fun createOperatorTask(parkingSlotSalesId: Long, user: User, scheduleDate: String) {
        val disposable =
            createOperatorUseCase.execute(
                CreateTaskOperatorUseCase.Params(
                    CreateTask(
                        parkingSlotSalesId,
                        user.id,
                        scheduleDate
                    )
                )
            )
                .subscribe({
                    viewEventObservable.postValue(OperatorViewEvent.CreateTaskOperatorSuccess)
                }, {
                    val errorMessage = it.message ?: "Failed"
                    viewEventObservable.postValue(
                        OperatorViewEvent.CreateTaskOperatorFailed(
                            errorMessage
                        )
                    )
                })
        addDisposable(disposable)
    }


    fun createOperatorTaskHomeVisit(trxVisitSalesId: Long, user: User) {
        val disposable =
            createOperatorHomeVisitUseCase.execute(
                CreateOperatorHomeVisitUseCase.Params(
                    trxVisitSalesId, user.id
                )
            )
                .subscribe({
                    viewEventObservable.postValue(OperatorViewEvent.CreateTaskOperatorHomeVisitSuccess)
                }, {
                    val errorMessage = it.message ?: "Failed"
                    viewEventObservable.postValue(
                        OperatorViewEvent.CreateTaskOperatorHomeVisitFailed(
                            errorMessage
                        )
                    )
                })
        addDisposable(disposable)
    }

    fun searchFoodTruck(keyword: String): ObservableSource<List<User>> {
        return searchOperatorUseCase.execute(SearchOperatorUseCase.Params(keyword))
            .onErrorReturn {
                arrayListOf()
            }
            .toObservable()
    }

    private fun handleCallback(useCase: OperatorViewEvent) {
        when (useCase) {
            is OperatorViewEvent.GetOperatorSuccess -> {
                useCase.data?.let {
                    viewEventObservable.postValue(useCase)
                }
            }
            is OperatorViewEvent.GetOperatorFailed -> {
                useCase.message?.let {
                    viewEventObservable.postValue(useCase)
                }
            }
        }
    }

}