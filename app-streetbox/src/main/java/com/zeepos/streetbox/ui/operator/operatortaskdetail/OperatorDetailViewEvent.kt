package com.zeepos.streetbox.ui.operator.operatortaskdetail

import com.zeepos.models.master.ParkingSlot
import com.zeepos.ui_base.ui.BaseViewEvent

sealed class OperatorDetailViewEvent : BaseViewEvent {
    object UpdateParkingSalesSuccess : OperatorDetailViewEvent()
    data class GetParkingDetailSuccess(val data: List<ParkingSlot>) :
        OperatorDetailViewEvent()

    object GetParkingDetailFailed : OperatorDetailViewEvent()

}