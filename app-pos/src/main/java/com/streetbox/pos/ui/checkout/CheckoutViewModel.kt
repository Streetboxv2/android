package com.streetbox.pos.ui.checkout

import com.zeepos.domain.interactor.order.GetOrderByUniqueIdUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 7/12/20
 */
class CheckoutViewModel @Inject constructor(
    private val getOrderByUniqueIdUseCase: GetOrderByUniqueIdUseCase
) : BaseViewModel<CheckoutViewEvent>() {
    fun getOrder(uniqueId: String) {
        val disposable =
            getOrderByUniqueIdUseCase.execute(GetOrderByUniqueIdUseCase.Params(uniqueId))
                .subscribe({
                    viewEventObservable.postValue(CheckoutViewEvent.GetOrderSuccess(it))
                }, { it.printStackTrace() })

        addDisposable(disposable)
    }
}