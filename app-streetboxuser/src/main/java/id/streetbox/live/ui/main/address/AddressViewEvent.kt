package id.streetbox.live.ui.main.address

import com.google.gson.JsonObject
import com.zeepos.models.response.ResponseListAddress
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewEvent

sealed class AddressViewEvent : BaseViewEvent {
    object OnSuccessAddAddress : AddressViewEvent()
    object OnSuccessAddress : AddressViewEvent()
    class OnSuccessListAddress(val responseListAddress: ResponseListAddress) : AddressViewEvent()
    data class OnFailedAddress(val throwable: Throwable) : AddressViewEvent()
    data class OnSuccessCallFoodTruck(val jsonObject: JsonObject) :
        AddressViewEvent()

}