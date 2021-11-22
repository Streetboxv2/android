package id.streetbox.live.ui.main.orderhistory.orderhistorydetail

import com.zeepos.models.entities.OrderHistory
import com.zeepos.models.entities.OrderHistoryDetail
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.main.orderhistory.history.HistoryViewEvent

/**
 * Created by Arif S. on 8/10/20
 */
sealed class OrderHistoryDetailViewEvent : BaseViewEvent {
    data class GetOrderSuccess(val orderHistoryDetail: OrderHistoryDetail) : OrderHistoryDetailViewEvent()
    data class GetOrderFailed(val throwable: Throwable) : OrderHistoryDetailViewEvent()
}