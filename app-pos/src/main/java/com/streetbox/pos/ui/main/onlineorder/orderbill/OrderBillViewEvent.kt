package com.streetbox.pos.ui.main.onlineorder.orderbill

import com.streetbox.pos.ui.checkout.checkoutdetail.CheckoutDetailViewEvent
import com.streetbox.pos.ui.main.order.OrderViewEvent
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import com.zeepos.models.transaction.QRCodeResponse
import com.zeepos.ui_base.ui.BaseViewEvent

sealed class OrderBillViewEvent : BaseViewEvent {
    data class GetProductSuccess(val productSales: ProductSales) : OrderBillViewEvent()
    data class GetOrderBillSuccess(val orderBill: OrderBill) : OrderBillViewEvent()
    object CloseOnlineOrderSuccess : OrderBillViewEvent()
    data class CloseOnlineOrderFailed(val errorMessage: String) : OrderBillViewEvent()
    object CloseOrderSuccess : OrderBillViewEvent()
    object OnRemoveProductSuccess : OrderBillViewEvent()

}