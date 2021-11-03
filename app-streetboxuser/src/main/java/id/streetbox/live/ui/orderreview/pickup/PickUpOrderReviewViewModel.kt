package id.streetbox.live.ui.orderreview.pickup

import com.zeepos.domain.interactor.order.GetRecentOpenOrCreateOrderUseCase
import com.zeepos.domain.interactor.order.UpdateOrderUseCase
import com.zeepos.domain.interactor.orderbill.CalculateOrderUseCase
import com.zeepos.domain.interactor.product.GetAllProductByMerchantIdUseCase
import com.zeepos.domain.interactor.productsales.CreateProductSalesUseCase
import com.zeepos.domain.interactor.productsales.RemoveQtyProductSalesUseCase
import com.zeepos.domain.interactor.productsales.UpdateOrRemoveProductSalesUseCase
import com.zeepos.domain.interactor.user.GetUserInfoUseCase
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseViewModel
import id.streetbox.live.ui.main.profile.ProfileViewEvent
import id.streetbox.live.ui.menu.MenuViewEvent
import javax.inject.Inject

/**
 * Created by Arif S. on 7/24/20
 */
class PickUpOrderReviewViewModel @Inject constructor(
    private val getRecentOpenOrCreateOrderUseCase: GetRecentOpenOrCreateOrderUseCase,
    private val updateOrRemoveProductSalesUseCase: UpdateOrRemoveProductSalesUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
) :
    BaseViewModel<PickupOrderReviewViewEvent>() {

    fun getRecentOrder(merchantId: Long) {
        val disposable = getRecentOpenOrCreateOrderUseCase.execute(
            GetRecentOpenOrCreateOrderUseCase.Params(merchantId)
        )
            .subscribe({
                viewEventObservable.postValue(PickupOrderReviewViewEvent.GetOrderSuccess(it))
            }, { viewEventObservable.postValue(PickupOrderReviewViewEvent.NoOrderFound) })

        addDisposable(disposable)
    }


    fun updateOrRemoveProductSales(productSales: ProductSales) {
        val disposable =
            updateOrRemoveProductSalesUseCase.execute(
                UpdateOrRemoveProductSalesUseCase.Params(
                    productSales
                )
            )
                .subscribe({
                    viewEventObservable.postValue(
                        PickupOrderReviewViewEvent.UpdateProductSalesSuccess
                    )
                }, { it.printStackTrace() })

        addDisposable(disposable)
    }

    fun updateOrder(order: Order) {
        val disposable = updateOrderUseCase.execute(UpdateOrderUseCase.Params(order))
            .subscribe({
                viewEventObservable.postValue(PickupOrderReviewViewEvent.UpdateOrderSuccess)
            }, {
                it.printStackTrace()
            })
        addDisposable(disposable)
    }


}