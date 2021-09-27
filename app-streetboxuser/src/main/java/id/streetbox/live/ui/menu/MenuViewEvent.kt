package id.streetbox.live.ui.menu

import com.zeepos.models.entities.AvailableHomeVisitBookDate
import com.zeepos.models.entities.Schedule
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/20/20
 */
sealed class MenuViewEvent : BaseViewEvent {
    data class GetProductSuccess(val data: List<Product>) : MenuViewEvent()
    data class GetProductFailed(val errorMessage: String) : MenuViewEvent()
    data class GetOrCreateOrderSuccess(val order: Order) : MenuViewEvent()
    data class GetMerchantScheduleSuccess(val scheduleList: List<Schedule>) : MenuViewEvent()
    data class GetMerchantScheduleFailed(val errorMessage: String) : MenuViewEvent()
    object OrderFailedCreated : MenuViewEvent()
    data class OnCalculateDone(val orderBill: OrderBill) : MenuViewEvent()
    object OnRemoveProductSuccess : MenuViewEvent()
    data class AddItemSuccess(val productSales: ProductSales) : MenuViewEvent()
    object UpdateProductSalesSuccess : MenuViewEvent()
    object GetTaxSuccess : MenuViewEvent()
    object GetTaxFailed : MenuViewEvent()
    data class GetFoodTruckHomeVisitDataSuccess(val data: List<AvailableHomeVisitBookDate>) :
        MenuViewEvent()

    data class GetFoodTruckHomeVisitDataFailed(val errorMessage: String) :
        MenuViewEvent()
}