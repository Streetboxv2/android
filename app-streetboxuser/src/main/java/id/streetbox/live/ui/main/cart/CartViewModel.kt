package id.streetbox.live.ui.main.cart

import com.zeepos.domain.interactor.order.GetCartUseCase
import com.zeepos.models.entities.None
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 6/13/20
 */
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase
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
}