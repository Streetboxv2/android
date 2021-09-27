package com.zeepos.domain.interactor.order

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.models.entities.None
import com.zeepos.models.transaction.Order
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 8/3/20
 */
class GetCartUseCase @Inject constructor(
    private val orderRepo: OrderRepo
) : SingleUseCase<Order, None>() {
    override fun buildUseCaseSingle(params: None): Single<Order> {
        return orderRepo.getCart()
    }
}