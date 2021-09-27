package com.zeepos.streetbox.ui.main.parkingspace

import com.zeepos.domain.interactor.parkingspace.GetAllParkingSpaceCloudUseCase
import com.zeepos.domain.interactor.parkingspace.GetAllParkingUseCase
import com.zeepos.domain.interactor.parkingspace.SearchParkingSpaceUseCase
import com.zeepos.models.entities.None
import com.zeepos.models.master.ParkingSpace
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.ObservableSource
import javax.inject.Inject

class ParkingSpaceViewModel @Inject constructor(
    private val getAllParkingSpaceCloudUseCase: GetAllParkingSpaceCloudUseCase,
    private val getAllParkingUseCase: GetAllParkingUseCase,
    private val searchParkingSpaceUseCase: SearchParkingSpaceUseCase
) : BaseViewModel<ParkingSpaceViewEvent>() {

    fun getParkingSpace() {
        val disposable = getAllParkingUseCase.execute(None())
            .subscribe({
                viewEventObservable.postValue(
                    ParkingSpaceViewEvent.GetAllParkingSpaceSuccess(it as MutableList<ParkingSpace>)
                )
            }, {
                it.message?.let { errorMessage ->
                    viewEventObservable.postValue(
                        ParkingSpaceViewEvent.GetAllParkingSpaceFailed(errorMessage)
                    )
                }
            })
        addDisposable(disposable)
    }

    fun getParkingSpaceCloud(isRefresh: Boolean = false) {
        addDisposable(
            getAllParkingSpaceCloudUseCase.execute(GetAllParkingSpaceCloudUseCase.Params(isRefresh))
                .subscribe(
                    {
                        viewEventObservable.postValue(
                            ParkingSpaceViewEvent.GetAllParkingSpaceSuccess(it as MutableList<ParkingSpace>)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                ParkingSpaceViewEvent.GetAllParkingSpaceFailed(it)
                            )
                        }
                    }
                )
        )
    }

    fun searchParkingSpace(keyword: String): ObservableSource<List<ParkingSpace>> {
        return searchParkingSpaceUseCase.execute(SearchParkingSpaceUseCase.Params(keyword))
            .doOnError { }
            .onErrorReturn {
                arrayListOf()
            }
            .toObservable()
    }

}