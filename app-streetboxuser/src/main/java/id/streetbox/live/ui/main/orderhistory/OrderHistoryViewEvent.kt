package id.streetbox.live.ui.main.orderhistory

import com.zeepos.models.entities.OrderHistory
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/13/20
 */
sealed class OrderHistoryViewEvent : BaseViewEvent {
    data class GetOrderHistorySuccess(val data: OrderHistory) : OrderHistoryViewEvent()
    data class GetOrderHistoryFailed(val errorMessage: String) : OrderHistoryViewEvent()
}