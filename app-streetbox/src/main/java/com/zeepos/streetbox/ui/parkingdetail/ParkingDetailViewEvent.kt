package com.zeepos.streetbox.ui.parkingdetail

import com.zeepos.models.master.ParkingSlot
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/15/20
 */
sealed class ParkingDetailViewEvent : BaseViewEvent {
    object UpdateParkingSalesSuccess : ParkingDetailViewEvent()
    data class GetParkingDetailSuccess(val data: List<ParkingSlot>) :
        ParkingDetailViewEvent()

    data class GetParkingDetailFailed(val errorMessage: String) : ParkingDetailViewEvent()

    data class GetParkingSalesDetailSuccess(val data: ParkingSales) :
        ParkingDetailViewEvent()

    object DisMissLoading : ParkingDetailViewEvent()

}