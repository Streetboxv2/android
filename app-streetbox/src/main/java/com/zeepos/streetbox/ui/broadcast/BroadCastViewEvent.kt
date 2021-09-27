package com.zeepos.streetbox.ui.broadcast

import com.google.gson.JsonObject
import com.zeepos.models.response.ResponseGetStatusCallFoodTruck
import com.zeepos.models.response.ResponseRuleBlast
import com.zeepos.ui_base.ui.BaseViewEvent

open class BroadCastViewEvent : BaseViewEvent {
    data class OnSuccessList(val dataItemGetStatusCallFoodTruck: ResponseGetStatusCallFoodTruck) :
        BroadCastViewEvent()

    data class OnSuccessListFinishOrder(val jsonObject: JsonObject) :
        BroadCastViewEvent()

    data class OnSuccessReqAutoBlast(val jsonObject: JsonObject) :
        BroadCastViewEvent()

    data class OnSuccessStatusOrder(val jsonObject: JsonObject) :
        BroadCastViewEvent()

    data class OnSuccessTimerGetBlast(val responseRuleBlast: ResponseRuleBlast) :
        BroadCastViewEvent()

    data class OnSuccessNotifBlastManual(val jsonObject: JsonObject) :
        BroadCastViewEvent()

    data class OnSuccessUpdateLoc(val jsonObject: JsonObject) :
        BroadCastViewEvent()

    data class OnFailed(val throwable: Throwable) : BroadCastViewEvent()
    data class OnFailedBlastNotif(val throwable: Throwable) : BroadCastViewEvent()

}