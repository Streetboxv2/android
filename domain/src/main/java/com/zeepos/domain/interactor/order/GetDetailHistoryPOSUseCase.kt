package com.zeepos.domain.interactor.order

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.models.entities.OrderHistoryDetail
import com.zeepos.models.transaction.AllTransaction
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import io.reactivex.Single
import javax.inject.Inject

class GetDetailHistoryPOSUseCase @Inject constructor(
    private val orderRepo: OrderRepo
) : SingleUseCase<AllTransaction, GetDetailHistoryPOSUseCase.Params>() {
    data class Params(val trxId:String)

    override fun buildUseCaseSingle(params: Params): Single<AllTransaction> {
        return  orderRepo.getOrderCloudPOS(params.trxId)
    }
}