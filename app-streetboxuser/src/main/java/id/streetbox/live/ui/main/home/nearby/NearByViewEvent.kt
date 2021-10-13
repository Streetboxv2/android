package id.streetbox.live.ui.main.home.nearby

import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.User
import com.zeepos.models.response.ResponseDistanceKm
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.orderreview.pickup.PickupOrderReviewViewEvent

/**
 * Created by Arif S. on 6/14/20
 */
sealed class NearByViewEvent : BaseViewEvent {
    data class GetNearBySuccess(val data: MutableList<FoodTruck>) : NearByViewEvent()
    data class GetSuccessDistanceKm(val dataDistance: ResponseDistanceKm) : NearByViewEvent()
    data class GetFailedDistanceKm(val throwable: Throwable) : NearByViewEvent()
    object GetNearByFailed : NearByViewEvent()

}