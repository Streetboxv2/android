package com.streetbox.pos.ui.checkout.checkoutdetail

import com.streetbox.pos.ui.main.MainViewEvent
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.QRCodeResponse
import com.zeepos.payment.PaymentViewEvent
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 7/12/20
 */
sealed class CheckoutDetailViewEvent : BaseViewEvent {
    data class GetOrderSuccess(val order: Order) : CheckoutDetailViewEvent()
    object CloseOrderSuccess : CheckoutDetailViewEvent()
    data class GetQRCodePaymentSuccess(val data: QRCodeResponse) : CheckoutDetailViewEvent()
    data class GetQRCodePaymentFailed(val errorMessage: String) : CheckoutDetailViewEvent()
    object OrderFailedCreated : CheckoutDetailViewEvent()
    data class GetAllTransactionSuccess(val orderList: List<Order>) : CheckoutDetailViewEvent()

}