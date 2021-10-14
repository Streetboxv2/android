package com.zeepos.payment

import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.QRCodeResponse
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/18/20
 */
sealed class PaymentViewEvent : BaseViewEvent {
    data class GetQRCodePaymentSuccess(val data: QRCodeResponse) : PaymentViewEvent()
    data class GetQRCodePaymentFailed(val errorMessage: String) : PaymentViewEvent()
    data class GetPaymentMethodSuccess(val data: List<PaymentMethod>) : PaymentViewEvent()
    data class GetPaymentMethodFailed(val errorMesge: String) : PaymentViewEvent()
    data class GetOrderSuccess(val order: Order) : PaymentViewEvent()
    data class GetOrderFailed(val throwable: Throwable) : PaymentViewEvent()
    object CloseOrderSuccess : PaymentViewEvent()
}