package com.streetbox.pos.ui.receipts

import com.zeepos.models.transaction.AllTransaction
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.Trx
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 10/7/20
 */
sealed class ReceiptViewEvent : BaseViewEvent {
    data class GetAllTransactionSuccess(val orderList: List<Order>) : ReceiptViewEvent()
    data class GetHistorySuccess(val orderList: List<Trx>) : ReceiptViewEvent()
    data class VoidOrderSuccess(val message: String) : ReceiptViewEvent()
}