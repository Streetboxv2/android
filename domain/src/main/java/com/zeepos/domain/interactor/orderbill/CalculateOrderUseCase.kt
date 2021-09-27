package com.zeepos.domain.interactor.orderbill

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.OrderBillRepo
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/3/20
 */
class CalculateOrderUseCase @Inject constructor(
    private val orderBillRepo: OrderBillRepo
) : SingleUseCase<OrderBill, CalculateOrderUseCase.Params>() {
    data class Params(val order: Order)

    override fun buildUseCaseSingle(params: Params): Single<OrderBill> {
        return orderBillRepo.calculateOrder(params.order)
    }
}