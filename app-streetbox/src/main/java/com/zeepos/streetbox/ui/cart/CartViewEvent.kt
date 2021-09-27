package com.zeepos.streetbox.ui.cart

import com.zeepos.models.transaction.ParkingSales
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/17/20
 */
sealed class CartViewEvent : BaseViewEvent {
    data class GetDataSuccess(val data: MutableList<ParkingSales>) : CartViewEvent()
    data class GetDataFailed(val errorMessage: String) : CartViewEvent()
}