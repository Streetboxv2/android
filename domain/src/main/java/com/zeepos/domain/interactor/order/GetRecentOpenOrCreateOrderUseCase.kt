package com.zeepos.domain.interactor.order

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.transaction.Order
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 5/17/20
 */
class GetRecentOpenOrCreateOrderUseCase @Inject constructor(
    private val orderRepo: OrderRepo
) : SingleUseCase<Order, GetRecentOpenOrCreateOrderUseCase.Params>() {

    data class Params(val merchantUsersId: Long = 0, val foodTruck: FoodTruck? = null)

    override fun buildUseCaseSingle(params: Params): Single<Order> {
        return orderRepo.getRecentOpenOrCreateOrder(params.merchantUsersId, params.foodTruck)
    }

}