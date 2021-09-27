package com.streetbox.pos.ui.main.onlineorder.productsales

import com.streetbox.pos.ui.main.onlineorder.orderbill.OrderBillViewEvent
import com.streetbox.pos.ui.main.onlineorder.paymentsales.ProductSalesViewEvent
import com.zeepos.domain.interactor.SearchListOrderUseCase
import com.zeepos.domain.interactor.GetOnlineOrderUseCase
import com.zeepos.domain.interactor.order.CloseOrderUseCase
import com.zeepos.domain.interactor.orderbill.GetOrderBillByUniqueIdUseCase
import com.zeepos.models.entities.None
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.ObservableSource
import javax.inject.Inject

class ProductSalesViewModel @Inject constructor(

    private val getOnlineOrderUseCase: GetOnlineOrderUseCase,
    private val getOrderBillByUniqueIdUseCase: GetOrderBillByUniqueIdUseCase,
    private val searchListOrderUseCase: SearchListOrderUseCase
) : BaseViewModel<ProductSalesViewEvent>() {

    fun getAllProductSales() {
        val data: MutableList<ProductSales> = mutableListOf()
        for (i in 1..20) {
            val productSales = ProductSales()
            productSales.no = i
            productSales.name = "Ditta"
            productSales.orderUniqueId = "0011"
            productSales.qty = 5
            productSales.notes = "Ga pake Lama"
            productSales.price = 5000.000
            productSales.subtotal = 5000.000
//            productSales.createDate = "18:20"

            data.add(productSales)
        }

        viewEventObservable.postValue(ProductSalesViewEvent.GetAllProductsSalesSuccess(data))
    }


    fun getAllOrderSales() {
        val data: MutableList<Order> = mutableListOf()
        for (i in 1..20) {
            val order = Order()
            order.no = i
            order.orderNo = "0011"
            order.uniqueId = "UU0111"
//            order.orderBill[0].billNo= "ut111"
//            paymentSales.createdAt = "18:20"

            data.add(order)
        }

        viewEventObservable.postValue(ProductSalesViewEvent.GetAllOrderSalesSuccess(data))
    }

    fun getOnlineOrder(){
        val disposable =
            getOnlineOrderUseCase.execute(None())
                .subscribe({
                    viewEventObservable.postValue(ProductSalesViewEvent.GetOnlineOrderSuccess(it.data!!))
                }, {
                    it.printStackTrace()})

        addDisposable(disposable)
    }


    fun getOrderBillByUniqueId(uniqueId: String) {
        val disposable =
            getOrderBillByUniqueIdUseCase.execute(GetOrderBillByUniqueIdUseCase.Params(uniqueId))
                .subscribe({
                    viewEventObservable.postValue(ProductSalesViewEvent.GetOrderBillSuccess(it))
                }, { it.printStackTrace() })

        addDisposable(disposable)
    }

    fun searchOrder(keyword: String): ObservableSource<List<Order>> {
        return searchListOrderUseCase.execute(SearchListOrderUseCase.Params(keyword))
            .doOnError { }
            .onErrorReturn {
                arrayListOf()
            }
            .toObservable()
    }

}