package com.zeepos.streetbox.ui.cart

import com.zeepos.domain.interactor.parkingsales.GetParkingSalesCloudUseCase
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 5/17/20
 */
class CartViewModel @Inject constructor(
    private val getParkingSalesCloudUseCase: GetParkingSalesCloudUseCase
) : BaseViewModel<CartViewEvent>() {

    fun getCartData(orderUniqueId: String) {
//        val disposable = getParkingSpaceSalesUseCase.execute(GetParkingSpaceSalesUseCase.Params(orderUniqueId))
//            .subscribe(
//                { values ->
//                    handleCallback(
//                        CartViewEvent.GetDataSuccess(
//                            (values.data
//                                ?: emptyArray<ParkingSales>()) as MutableList<ParkingSales>
//                        )
//                    )
//                },
//                { error ->
//                    handleCallback(CartViewEvent.GetDataFailed(error.message ?: ""))
//                }
//            )
//        addDisposable(disposable)
    }

    private fun handleCallback(useCase: CartViewEvent) {
        viewEventObservable.postValue(useCase)
    }
}