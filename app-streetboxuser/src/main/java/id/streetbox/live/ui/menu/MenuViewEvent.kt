package id.streetbox.live.ui.menu

import com.zeepos.models.entities.AvailableHomeVisitBookDate
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.entities.Schedule
import com.zeepos.models.master.Product
import com.zeepos.models.master.Tax
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import com.zeepos.models.transaction.TaxSales
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.main.home.nearby.NearByViewEvent

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
    data class GetTaxSuccess(val tax: Tax) : MenuViewEvent()
    object GetTaxFailed : MenuViewEvent()
    data class GetFoodTruckHomeVisitDataSuccess(val data: List<AvailableHomeVisitBookDate>) :
        MenuViewEvent()

    data class GetFoodTruckHomeVisitDataFailed(val errorMessage: String) :
        MenuViewEvent()

    data class GetUserInfoSuccess(val user: User) : MenuViewEvent()
    data class GetUserInfoFailed(val errorMessage: String) : MenuViewEvent()
    data class GetTaxSettingSuccess(val products: ResponseApi<TaxSales>) : MenuViewEvent()

}