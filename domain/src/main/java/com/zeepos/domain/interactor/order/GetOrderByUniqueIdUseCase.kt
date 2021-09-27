package com.zeepos.domain.interactor.order

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.models.transaction.Order
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/12/20
 */
class GetOrderByUniqueIdUseCase @Inject constructor(
    private val orderRepo: OrderRepo
) : SingleUseCase<Order, GetOrderByUniqueIdUseCase.Params>() {
    data class Params(val orderUniqueId: String)

    override fun buildUseCaseSingle(params: Params): Single<Order> {
        return Single.fromCallable { orderRepo.getOrder(params.orderUniqueId) }
    }
}