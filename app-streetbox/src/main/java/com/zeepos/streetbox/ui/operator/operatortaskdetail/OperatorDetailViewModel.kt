package com.zeepos.streetbox.ui.operator.operatortaskdetail

import com.zeepos.domain.interactor.parkingsales.CreateOrUpdateParkingSalesUseCase
import com.zeepos.domain.interactor.parkingsales.GetParkingSalesByParkingSpaceIdUseCase
import com.zeepos.domain.interactor.parkingspace.GetParkingDetailUseCase
import com.zeepos.domain.interactor.parkingspace.GetParkingSpaceUseCase
import com.zeepos.models.master.ParkingSlot
import com.zeepos.models.master.ParkingSpace
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.models.transaction.ParkingSlotSales
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class OperatorDetailViewModel @Inject constructor(
    private val createOrUpdateParkingSalesUseCase: CreateOrUpdateParkingSalesUseCase,
    private val getParkingSpaceUseCase: GetParkingSpaceUseCase,
    private val getParkingDetailUseCase: GetParkingDetailUseCase,
    private val getParkingSalesByParkingSpaceIdUseCase: GetParkingSalesByParkingSpaceIdUseCase
) : BaseViewModel<OperatorDetailViewEvent>() {

    fun getParkingSales(parkingSpaceId: Long): ParkingSales? {
        return getParkingSalesByParkingSpaceIdUseCase.execute(
            GetParkingSalesByParkingSpaceIdUseCase.Params(parkingSpaceId)
        )
    }

    fun getParkingSpace(id: Long): ParkingSpace? {
        return getParkingSpaceUseCase.execute(GetParkingSpaceUseCase.Params(id))
    }

    fun getParkingDetail(id: Long) {
        val disposable = getParkingDetailUseCase.execute(GetParkingDetailUseCase.Params(id))
            .subscribe({
                viewEventObservable.postValue(
                    OperatorDetailViewEvent.GetParkingDetailSuccess(it)
                )
            }, {
                viewEventObservable.postValue(OperatorDetailViewEvent.GetParkingDetailFailed)
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
            viewEventObservable.postValue(OperatorDetailViewEvent.UpdateParkingSalesSuccess)
        }

        addDisposable(disposable)
    }

    fun increaseSlotQty(parkingSlotSales: ParkingSlotSales) {

    }

    fun decreaseSlotQty(slotId: Long) {

    }
}