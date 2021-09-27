package com.zeepos.domain.interactor.order

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.models.transaction.Order
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 7/28/20
 */
class UpdateOrderUseCase @Inject constructor(
    private val orderRepo: OrderRepo
) : CompletableUseCase<UpdateOrderUseCase.Params>() {
    data class Params(val order: Order)

    override fun buildUseCaseCompletable(params: Params): Completable {
        return orderRepo.update(params.order)
    }
}