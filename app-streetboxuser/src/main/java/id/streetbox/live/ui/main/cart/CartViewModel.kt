package id.streetbox.live.ui.main.cart

import com.zeepos.domain.interactor.GetTaxUseCase
import com.zeepos.domain.interactor.order.GetCartUseCase
import com.zeepos.domain.interactor.order.GetRecentOpenOrCreateOrderUseCase
import com.zeepos.domain.interactor.order.UpdateOrderUseCase
import com.zeepos.domain.interactor.orderbill.CalculateOrderUseCase
import com.zeepos.models.entities.None
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewModel
import id.streetbox.live.ui.menu.MenuViewEvent
import id.streetbox.live.ui.orderreview.pickup.PickupOrderReviewViewEvent
import javax.inject.Inject

/**
 * Created by Arif S. on 6/13/20
 */
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val calculateOrderUseCase: CalculateOrderUseCase,
    private val getTaxUseCase: GetTaxUseCase,
    private val getRecentOpenOrCreateOrderUseCase: GetRecentOpenOrCreateOrderUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase
) : BaseViewModel<CartViewEvent>() {
    fun getCartData() {
        val disposable = getCartUseCase.execute(None())
            .subscribe({
                viewEventObservable.postValue(CartViewEvent.GetCartDataSuccess(it))
            }, {
                viewEventObservable.postValue(CartViewEvent.NoDataInCart)
            })

        addDisposable(disposable)

    }

    fun updateOrder(order: Order) {
        val disposable = updateOrderUseCase.execute(UpdateOrderUseCase.Params(order))
            .subscribe({
                viewEventObservable.postValue(CartViewEvent.UpdateOrderSuccess)
            }, {
                it.printStackTrace()
            })
        addDisposable(disposable)
    }

    fun getRecentOrder(merchantId: Long) {
        val disposable = getRecentOpenOrCreateOrderUseCase.execute(
            GetRecentOpenOrCreateOrderUseCase.Params(merchantId)
        )
            .subscribe({
                viewEventObservable.postValue(CartViewEvent.GetOrCreateOrderSuccess(it))
            },
                { viewEventObservable.postValue(CartViewEvent.OrderFailedCreated) })

        addDisposable(disposable)
    }


    fun getMerchantTax(merchantId: Long) {
        val disposable = getTaxUseCase.execute(GetTaxUseCase.Params(merchantId))
            .subscribe({
                viewEventObservable.postValue(CartViewEvent.GetTaxSuccess(it))
            },
                {
                    it.printStackTrace()

                })
        addDisposable(disposable)
    }
}