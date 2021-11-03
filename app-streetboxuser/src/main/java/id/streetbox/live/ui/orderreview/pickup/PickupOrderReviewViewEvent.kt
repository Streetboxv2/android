package id.streetbox.live.ui.orderreview.pickup

import com.zeepos.models.master.Product
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.main.profile.ProfileViewEvent
import id.streetbox.live.ui.menu.MenuViewEvent

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
    data class GetProductSuccess(val data: List<Product>) : PickupOrderReviewViewEvent()
    data class GetProductFailed(val errorMessage: String) : PickupOrderReviewViewEvent()
    data class OnCalculateDone(val orderBill: OrderBill) : PickupOrderReviewViewEvent()
    data class AddItemSuccess(val productSales: ProductSales) : PickupOrderReviewViewEvent()
}