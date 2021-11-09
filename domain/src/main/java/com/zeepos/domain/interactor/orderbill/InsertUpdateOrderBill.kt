package com.zeepos.domain.interactor.orderbill

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.interactor.order.UpdateOrderUseCase
import com.zeepos.domain.repository.OrderBillRepo
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class InsertUpdateOrderBill @Inject constructor(
    private val orderBillRepo: OrderBillRepo
) : CompletableUseCase<InsertUpdateOrderBill.Params>() {
    data class Params(val orderBill: OrderBill)

    override fun buildUseCaseCompletable(params: Params): Completable {
        return orderBillRepo.insertUpdateOrderBill(params.orderBill)
    }
}