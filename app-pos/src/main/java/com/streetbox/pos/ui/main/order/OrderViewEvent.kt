package com.streetbox.pos.ui.main.order

import com.zeepos.models.transaction.OrderBill
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 2019-11-02
 */
sealed class OrderViewEvent : BaseViewEvent {
    data class OnCalculateDone(val orderBill: OrderBill) : OrderViewEvent()
    object OnRemoveProductSuccess : OrderViewEvent()
    object OnRemoveProductQtySuccess : OrderViewEvent()
}