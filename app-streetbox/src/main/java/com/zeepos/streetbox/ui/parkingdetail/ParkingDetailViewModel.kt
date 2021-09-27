package com.zeepos.streetbox.ui.parkingdetail

import com.zeepos.domain.interactor.parkingsales.CreateOrUpdateParkingSalesUseCase
import com.zeepos.domain.interactor.parkingsales.GetParkingSalesByParkingSpaceIdUseCase
import com.zeepos.domain.interactor.parkingsales.GetParkingSalesDetailUseCase
import com.zeepos.domain.interactor.parkingspace.GetParkingDetailUseCase
import com.zeepos.domain.interactor.parkingspace.GetParkingSpaceUseCase
import com.zeepos.models.ConstVar
import com.zeepos.models.master.ParkingSlot
import com.zeepos.models.master.ParkingSpace
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 5/15/20
 */
class ParkingDetailViewModel @Inject constructor(
    private val createOrUpdateParkingSalesUseCase: CreateOrUpdateParkingSalesUseCase,
    private val getParkingSpaceUseCase: GetParkingSpaceUseCase,
    private val getParkingDetailUseCase: GetParkingDetailUseCase,
    private val getParkingSalesDetailUseCase: GetParkingSalesDetailUseCase,
    private val getParkingSalesByParkingSpaceIdUseCase: GetParkingSalesByParkingSpaceIdUseCase
) : BaseViewModel<ParkingDetailViewEvent>() {

    fun getParkingSalesDetail(parkingSalesId: Long) {
        val disposable = getParkingSalesDetailUseCase.execute(
            GetParkingSalesDetailUseCase.Params(parkingSalesId)
        )
            .subscribe({
                viewEventObservable.postValue(ParkingDetailViewEvent.GetParkingSalesDetailSuccess(it))
            }, {
                it.printStackTrace()
                viewEventObservable.value = ParkingDetailViewEvent.DisMissLoading
            })

        addDisposable(disposable)
    }

    fun getParkingSpace(id: Long): ParkingSpace? {
        return getParkingSpaceUseCase.execute(GetParkingSpaceUseCase.Params(id))
    }

    fun getParkingDetail(id: Long) {
        val disposable = getParkingDetailUseCase.execute(GetParkingDetailUseCase.Params(id))
            .subscribe({
                viewEventObservable.postValue(
                    ParkingDetailViewEvent.GetParkingDetailSuccess(it)
                )
            }, {
                val errorMessage = it.message ?: ConstVar.ERROR_MESSAGE
                viewEventObservable.postValue(
                    ParkingDetailViewEvent.GetParkingDetailFailed(
                        errorMessage
                    )
                )
            })

        addDisposable(disposable)
    }

    fun createOrUpdateParkingSales(parkingSpace: ParkingSpace, parkingSlot: ParkingSlot) {
        val disposable = createOrUpdateParkingSalesUseCase.execute(
            CreateOrUpdateParkingSalesUseCase.Params(
                parkingSpace,
                parkingSlot
            )
        ).subscribe {
            viewEventObservable.postValue(ParkingDetailViewEvent.UpdateParkingSalesSuccess)
        }

        addDisposable(disposable)
    }

}