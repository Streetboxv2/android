package com.zeepos.domain.interactor.order

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.models.entities.OrderHistory
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/31/20
 */
class GetAllOrderUseCase @Inject constructor(
    private val orderRepo: OrderRepo
) : SingleUseCase<List<OrderHistory>, GetAllOrderUseCase.Params>() {
    override fun buildUseCaseSingle(params: Params): Single<List<OrderHistory>> {
        return orderRepo.getOrderCloud(params.page, params.filter)
    }

    data class Params(val page: Int, val filter: String)
}