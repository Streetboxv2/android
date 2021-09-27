package id.streetbox.live.ui.main.cart

import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/13/20
 */
sealed class CartViewEvent : BaseViewEvent {
    data class GetCartDataSuccess(val data: Order) : CartViewEvent()
    object NoDataInCart : CartViewEvent()
}