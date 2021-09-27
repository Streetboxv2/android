package id.streetbox.live.ui.main.doortodoor

import com.google.gson.JsonObject
import com.zeepos.models.response.ResponseAddressPrimary
import com.zeepos.models.response.ResponseGetLocFoodTruck
import com.zeepos.models.response.ResponseGetStatusCall
import com.zeepos.models.response.ResponseListNotificationBlast
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.bookhomevisit.BookHomeVisitViewEvent

sealed class DoortoDoorViewEvent : BaseViewEvent {
    data class OnSuccessList(val responseGetStatusCall: ResponseGetStatusCall) :
        DoortoDoorViewEvent()

    data class OnSuccessListNotif(val responseListNotificationBlast: ResponseListNotificationBlast) :
        DoortoDoorViewEvent()

    data class OnSuccessCallFoodTruck(val jsonObject: JsonObject) :
        DoortoDoorViewEvent()

    data class OnFailed(val throwable: Throwable) : DoortoDoorViewEvent()


    data class OnFailedAddress(val throwable: Throwable) : DoortoDoorViewEvent()

    data class OnSuccessListAddress(val responseListAddress: ResponseAddressPrimary) :
        DoortoDoorViewEvent()


    data class OnSuccessGetLocFoodTruck(val jsonObject: ResponseGetLocFoodTruck) :
        DoortoDoorViewEvent()


}