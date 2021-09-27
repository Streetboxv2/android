package com.zeepos.domain.interactor.orderbill

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.interactor.order.GetOrderByUniqueIdUseCase
import com.zeepos.domain.repository.OrderBillRepo
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.domain.repository.ProductSalesRepo
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.entities.None
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import io.reactivex.Single
import javax.inject.Inject

class GetOrderBillByUniqueIdUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : SingleUseCase<OrderBill, GetOrderBillByUniqueIdUseCase.Params>() {
    data class Params(val orderUniqueId: String)

    override fun buildUseCaseSingle(params: Params): Single<OrderBill> {
        return Single.fromCallable { syncDataRepo.createOrderBill(params.orderUniqueId) }
    }
}