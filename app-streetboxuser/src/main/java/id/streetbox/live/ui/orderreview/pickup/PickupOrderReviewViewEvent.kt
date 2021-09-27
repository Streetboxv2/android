package id.streetbox.live.ui.orderreview.pickup

import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 7/24/20
 */
sealed class PickupOrderReviewViewEvent : BaseViewEvent {
    object UpdateProductSalesSuccess : PickupOrderReviewViewEvent()
    data class GetOrderSuccess(val order: Order) : PickupOrderReviewViewEvent()
    object UpdateOrderSuccess : PickupOrderReviewViewEvent()
    object OnRemoveProductSalesQtySuccess : PickupOrderReviewViewEvent()
    object NoOrderFound : PickupOrderReviewViewEvent()
}