package id.streetbox.live.ui.main

import com.zeepos.models.master.User
import com.zeepos.models.transaction.OrderBill
import com.zeepos.ui_base.ui.BaseViewEvent
import id.streetbox.live.ui.menu.MenuViewEvent

/**
 * Created by Arif S. on 6/9/20
 */
sealed class MainViewEvent : BaseViewEvent {
    data class GetProfileSuccess(val data: User) : BaseViewEvent
    data class GetProfileFailed(val message: String?) : BaseViewEvent
    data class OnCalculateDone(val orderBill: OrderBill) : MainViewEvent()
}