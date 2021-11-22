package id.streetbox.live.ui.main.orderhistory.history

import com.zeepos.models.entities.OrderHistory
import com.zeepos.models.transaction.Order
import com.zeepos.payment.PaymentViewEvent
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.main.orderhistory.OrderHistoryViewEvent

sealed class HistoryViewEvent : BaseViewEvent {
    data class GetOrderHistorySuccess(val data: List<OrderHistory>) : HistoryViewEvent()
    data class GetOrderHistoryFailed(val errorMessage: String) : HistoryViewEvent()

}