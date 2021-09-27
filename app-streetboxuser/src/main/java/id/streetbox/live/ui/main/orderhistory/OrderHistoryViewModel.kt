package id.streetbox.live.ui.main.orderhistory

import com.zeepos.domain.interactor.order.GetAllOrderUseCase
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 6/13/20
 */
class OrderHistoryViewModel @Inject constructor(
    private val getAllOrderUseCase: GetAllOrderUseCase
) : BaseViewModel<OrderHistoryViewEvent>() {

}