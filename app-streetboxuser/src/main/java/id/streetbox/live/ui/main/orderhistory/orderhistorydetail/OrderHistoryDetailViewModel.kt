package id.streetbox.live.ui.main.orderhistory.orderhistorydetail

import com.zeepos.domain.interactor.order.GetOrderHistoryIdUseCase
import id.streetbox.live.ui.main.orderhistory.OrderHistoryViewEvent
import com.zeepos.ui_base.ui.BaseViewModel
import id.streetbox.live.ui.main.orderhistory.history.HistoryViewEvent
import javax.inject.Inject

/**
 * Created by Arif S. on 8/10/20
 */
class OrderHistoryDetailViewModel @Inject constructor(
    private val getOrderHistoryIdUseCase: GetOrderHistoryIdUseCase
) : BaseViewModel<OrderHistoryDetailViewEvent>() {

    fun getOrderHistoryId(trxId: String) {
        val disposable =
            getOrderHistoryIdUseCase.execute(GetOrderHistoryIdUseCase.Params(trxId))
                .subscribe({
                    viewEventObservable.postValue(OrderHistoryDetailViewEvent.GetOrderSuccess(it))
                }, {
                    viewEventObservable.postValue(OrderHistoryDetailViewEvent.GetOrderFailed(it))
                })

        addDisposable(disposable)
    }
}