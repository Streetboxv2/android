package id.streetbox.live.ui.orderreview.pickup

import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.main.profile.ProfileViewEvent

/**
 * Created by Arif S. on 7/24/20
 */
sealed class PickupOrderReviewViewEvent : BaseViewEvent {
    object UpdateProductSalesSuccess : PickupOrderReviewViewEvent()
    data class GetOrderSuccess(val order: Order) : PickupOrderReviewViewEvent()
    object UpdateOrderSuccess : PickupOrderReviewViewEvent()
    object OnRemoveProductSalesQtySuccess : PickupOrderReviewViewEvent()
    object NoOrderFound : PickupOrderReviewViewEvent()
    data class GetUserInfoSuccess(val user: User) : PickupOrderReviewViewEvent()
    data class GetUserInfoFailed(val errorMessage: String) : PickupOrderReviewViewEvent()
}