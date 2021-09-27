package id.streetbox.live.ui.main.orderhistory.history

import com.zeepos.domain.interactor.order.GetAllOrderUseCase
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val getAllOrderUseCase: GetAllOrderUseCase
) : BaseViewModel<HistoryViewEvent>() {
    fun getOrderHistory(page: Int, filter: String) {
        val disposable = getAllOrderUseCase.execute(GetAllOrderUseCase.Params(page, filter))
            .subscribe({
                viewEventObservable.postValue(HistoryViewEvent.GetOrderHistorySuccess(it))
            }, {
                it.message?.let { errorMessage ->
                    viewEventObservable.postValue(
                        HistoryViewEvent.GetOrderHistoryFailed(
                            errorMessage
                        )
                    )
                }
            })

        addDisposable(disposable)
    }
}