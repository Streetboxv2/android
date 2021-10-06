package com.streetbox.pos.ui.main.onlineorder

import androidx.lifecycle.MutableLiveData
import com.streetbox.pos.ui.main.MainViewEvent
import com.zeepos.domain.interactor.GetProductUniqueIdUseCase
import com.zeepos.domain.interactor.order.GetAllOrderUseCase
import com.zeepos.domain.interactor.order.GetOrderByUniqueIdUseCase
import com.zeepos.domain.interactor.order.GetRecentOpenOrCreateOrderUseCase
import com.zeepos.domain.interactor.orderbill.GetOrderBillByUniqueIdUseCase
import com.zeepos.domain.interactor.productsales.GetRecentOrderUseCase
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class OnlineOrderViewModel @Inject constructor(
    private val getOrderBillByUniqueIdUseCase: GetOrderBillByUniqueIdUseCase,
    private val getProductUniqueIdUseCase: GetProductUniqueIdUseCase,
    private val getOrderByUniqueIdUseCase: GetOrderByUniqueIdUseCase,
    private val getRecentOpenOrCreateOrderUseCase: GetRecentOpenOrCreateOrderUseCase

    ) : BaseViewModel<OnlineOrderViewEvent>() {

    val productSalesObserver = MutableLiveData<ProductSales>()
    val orderBillObserver = MutableLiveData<OrderBill>()
    val orderObserver = MutableLiveData<Order>()
    fun getOrderBill(orderUniqueId: String) {
        val disposable = getOrderBillByUniqueIdUseCase.execute(GetOrderBillByUniqueIdUseCase.Params(orderUniqueId))
            .subscribe({
                orderBillObserver.value = it
            }, { it.printStackTrace() })

        addDisposable(disposable)

    }

    fun getRecentOrder() {
        val disposable =
            getRecentOpenOrCreateOrderUseCase.execute(GetRecentOpenOrCreateOrderUseCase.Params())
                .subscribe({
                    orderObserver.postValue(it)
                }, { viewEventObservable.postValue(OnlineOrderViewEvent.OrderFailedCreated) })

        addDisposable(disposable)
    }


    fun getProduct(orderUniqueId: String) {
        val disposable = getProductUniqueIdUseCase.execute(GetProductUniqueIdUseCase.Params(orderUniqueId))
            .subscribe({
                productSalesObserver.value = it
            }, { it.printStackTrace() })

        addDisposable(disposable)
    }

    fun getOrder(orderUniqueId: String) {
        val disposable = getOrderByUniqueIdUseCase.execute(GetOrderByUniqueIdUseCase.Params(orderUniqueId))
            .subscribe({
                orderObserver.value = it
            }, { it.printStackTrace() })

        addDisposable(disposable)

    }

}