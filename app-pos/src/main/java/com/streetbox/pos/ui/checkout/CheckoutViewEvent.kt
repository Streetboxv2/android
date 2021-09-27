package com.streetbox.pos.ui.checkout

import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 7/12/20
 */
sealed class CheckoutViewEvent : BaseViewEvent {
    data class GetOrderSuccess(val order: Order) : CheckoutViewEvent()
}