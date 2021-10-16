package com.streetbox.pos.ui.main

import com.streetbox.pos.ui.checkout.checkoutdetail.CheckoutDetailViewEvent
import com.streetbox.pos.ui.main.product.ProductViewEvent
import com.streetbox.pos.ui.receipts.ReceiptViewEvent
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Product
import com.zeepos.models.master.Tax
import com.zeepos.models.transaction.AllTransaction
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/3/20
 */
sealed class MainViewEvent : BaseViewEvent {
    object OrderFailedCreated : MainViewEvent()
    data class OnAddItemFailed(val errorMessage: String) : MainViewEvent()
    data class GetAllTransactionSuccess(val orderList: List<Order>) : MainViewEvent()

}