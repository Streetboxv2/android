package id.streetbox.live.ui.main.cart

import com.zeepos.models.master.Tax
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.menu.MenuViewEvent

/**
 * Created by Arif S. on 6/13/20
 */
sealed class CartViewEvent : BaseViewEvent {
    data class GetCartDataSuccess(val data: Order) : CartViewEvent()
    object NoDataInCart : CartViewEvent()
    data class GetTaxSuccess(val tax: Tax) : CartViewEvent()
}