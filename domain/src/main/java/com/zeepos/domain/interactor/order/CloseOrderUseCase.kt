package com.zeepos.domain.interactor.order

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.OrderRepo
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 7/15/20
 */
class CloseOrderUseCase @Inject constructor(
    private val orderRepo: OrderRepo
) : CompletableUseCase<CloseOrderUseCase.Params>() {
    data class Params(val uniqueId: String)

    override fun buildUseCaseCompletable(params: Params): Completable {
        return orderRepo.closeOrder(params.uniqueId)
    }
}