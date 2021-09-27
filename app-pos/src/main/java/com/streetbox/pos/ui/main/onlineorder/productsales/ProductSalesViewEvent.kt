package com.streetbox.pos.ui.main.onlineorder.paymentsales

import com.streetbox.pos.ui.main.onlineorder.orderbill.OrderBillViewEvent
import com.zeepos.models.transaction.*
import com.zeepos.ui_base.ui.BaseViewEvent

sealed class ProductSalesViewEvent : BaseViewEvent {
    data class GetAllProductsSalesSuccess(val products: List<ProductSales>) : ProductSalesViewEvent()
    data class GetAllProductSalesSuccess(val payment: List<PaymentSales>) : ProductSalesViewEvent()
    data class GetAllOrderSalesSuccess(val order: List<Order>) : ProductSalesViewEvent()
    data class GetOnlineOrderSuccess(val onlineOrder: OnlineOrder) : ProductSalesViewEvent()
    data class GetOrderBillSuccess(val order: OrderBill) : ProductSalesViewEvent()
    object CloseOrderSuccess : ProductSalesViewEvent()
}