package com.streetbox.pos.ui.main.product

import com.streetbox.pos.ui.checkout.checkoutdetail.CheckoutDetailViewEvent
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.OnlineOrder
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.TaxSales
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 2019-11-02
 */
sealed class ProductViewEvent : BaseViewEvent {
    data class GetAllProductsSuccess(val products: List<Product>) : ProductViewEvent()
    data class GetAllTransactionSuccess(val list: List<Order>) : ProductViewEvent()
    object GetSyncSuccess : ProductViewEvent()
    data class GetOnlineOrderSuccess(val onlineOrder: OnlineOrder) : ProductViewEvent()
    data class GetTaxSuccess(val products: ResponseApi<TaxSales>) : ProductViewEvent()

}