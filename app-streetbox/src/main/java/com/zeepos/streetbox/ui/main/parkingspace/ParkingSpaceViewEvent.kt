package com.zeepos.streetbox.ui.main.parkingspace

import com.zeepos.models.master.ParkingSpace
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/13/20
 */
sealed class ParkingSpaceViewEvent : BaseViewEvent {
    data class GetAllParkingSpaceSuccess(val data: MutableList<ParkingSpace>) :
        ParkingSpaceViewEvent()

    data class GetAllParkingSpaceFailed(val errorMessage: String) : ParkingSpaceViewEvent()
}