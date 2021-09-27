package com.streetbox.pos.ui.main.order

import com.zeepos.domain.interactor.orderbill.CalculateOrderUseCase
import com.zeepos.domain.interactor.productsales.GetRecentOrderUseCase
import com.zeepos.domain.interactor.productsales.RemoveProductSalesUseCase
import com.zeepos.domain.interactor.productsales.RemoveQtyProductSalesUseCase
import com.zeepos.models.entities.None
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 2019-11-02
 */
class OrderViewModel @Inject constructor(
    private val removeProductSalesUseCase: RemoveProductSalesUseCase,
    private val getRecentOrderUseCase: GetRecentOrderUseCase,
    private val calculateOrderUseCase: CalculateOrderUseCase,
    private val removeQtyProductSalesUseCase: RemoveQtyProductSalesUseCase
) : BaseViewModel<OrderViewEvent>() {

    fun getProductSales(orderUniqueId: String) {
        val disposable = getRecentOrderUseCase.execute(None())
            .subscribe({
            }, { it.printStackTrace() })

        addDisposable(disposable)

    }

    fun removeProductSales(productSales: ProductSales) {
        val disposable =
            removeProductSalesUseCase.execute(RemoveProductSalesUseCase.Params(productSales))
                .subscribe {
                    viewEventObservable.postValue(OrderViewEvent.OnRemoveProductSuccess)
                }

        addDisposable(disposable)
    }

    fun calculateOrder(order: Order) {
        val disposable = calculateOrderUseCase.execute(CalculateOrderUseCase.Params(order))
            .subscribe({
                viewEventObservable.postValue(OrderViewEvent.OnCalculateDone(it))
            }, { it.printStackTrace() })

        addDisposable(disposable)
    }


    fun removeQtyProduct(productSales: ProductSales){
        val disposable =
            removeQtyProductSalesUseCase.execute(RemoveQtyProductSalesUseCase.Params(productSales))
                .subscribe {
                    viewEventObservable.postValue(OrderViewEvent.OnRemoveProductQtySuccess)
                }

        addDisposable(disposable)
    }

}