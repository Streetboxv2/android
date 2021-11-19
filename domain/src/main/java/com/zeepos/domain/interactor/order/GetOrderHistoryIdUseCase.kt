package com.zeepos.domain.interactor.order

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.models.entities.OrderHistory
import com.zeepos.models.entities.OrderHistoryDetail
import com.zeepos.models.transaction.Order
import io.reactivex.Single
import javax.inject.Inject

class GetOrderHistoryIdUseCase @Inject constructor(
    private val orderRepo: OrderRepo
) : SingleUseCase<OrderHistoryDetail, GetOrderHistoryIdUseCase.Params>() {
    data class Params(val trxId:String)

    override fun buildUseCaseSingle(params: Params): Single<OrderHistoryDetail> {
        return  orderRepo.getOrderCloudId(params.trxId)
    }
}