package com.zeepos.streetbox.ui.main.myparkingspace

import com.zeepos.domain.interactor.operator.task.GetTaskOperatorByParkingSalesUseCase
import com.zeepos.domain.interactor.parkingsales.GetParkingSalesCloudUseCase
import com.zeepos.domain.interactor.parkingsales.GetParkingSalesUseCase
import com.zeepos.domain.interactor.parkingsales.SearchParkingSalesUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.None
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.ObservableSource
import javax.inject.Inject

/**
 * Created by Arif S. on 5/21/20
 */
class MyParkingSpaceViewModel @Inject constructor(
    private val getParkingSalesCloudUseCase: GetParkingSalesCloudUseCase,
    private val searchParkingSalesUseCase: SearchParkingSalesUseCase,
    private val getTaskOperatorByParkingSalesUseCase: GetTaskOperatorByParkingSalesUseCase,
    private val taskOperatorRepo: TaskOperatorRepo,
    private val getParkingSalesUseCase: GetParkingSalesUseCase
) : BaseViewModel<MyParkingSpaceViewEvent>() {

    fun getParkingSales() {
        val disposable = getParkingSalesUseCase.execute(None())
            .subscribe({
                viewEventObservable.postValue(
                    MyParkingSpaceViewEvent.GetAllParkingSalesSuccess(it)
                )
            }, {
                it.message?.let { errorMessage ->
                    viewEventObservable.postValue(
                        MyParkingSpaceViewEvent.GetAllParkingSpaceFailed(errorMessage)
                    )
                }
            })

        addDisposable(disposable)
    }

    fun getParkingSalesCloud(isRefresh: Boolean = false) {
        addDisposable(
            getParkingSalesCloudUseCase.execute(GetParkingSalesCloudUseCase.Params(isRefresh))
                .subscribe({
                    viewEventObservable.postValue(
                        MyParkingSpaceViewEvent.GetAllParkingSalesSuccess(it)
                    )
                }, { error ->
                    error.message?.let {
                        viewEventObservable.postValue(
                            MyParkingSpaceViewEvent.GetAllParkingSpaceFailed(it)
                        )
                    }
                })
        )
    }

    fun searchParkingSpace(keyword: String): ObservableSource<List<ParkingSales>> {
        return searchParkingSalesUseCase.execute(SearchParkingSalesUseCase.Params(keyword))
            .doOnError { }
            .onErrorReturn {
                arrayListOf()
            }
            .toObservable()
    }

    fun isTaskOperatorExists(parkingSalesId: Long): Boolean {
        val taskOperators = taskOperatorRepo.getByParkingSalesId(parkingSalesId)
        return taskOperators.isNotEmpty()
    }

    fun getParkingTask(parkingSalesId: Long) {
        val disposable = getTaskOperatorByParkingSalesUseCase.execute(
            GetTaskOperatorByParkingSalesUseCase.Params(parkingSalesId)
        )
            .subscribe({
                viewEventObservable.postValue(MyParkingSpaceViewEvent.GetTaskOperatorSuccess(it))
            }, {
                viewEventObservable.postValue(MyParkingSpaceViewEvent.GetTaskOperatorFailed)
            })
        addDisposable(disposable)
    }
}