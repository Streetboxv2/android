package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.transaction.Order
import io.reactivex.Single
import javax.inject.Inject

class SearchListOrderUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : SingleUseCase<List<Order>, SearchListOrderUseCase.Params>() {
    override fun buildUseCaseSingle(params: Params): Single<List<Order>> {
        return syncDataRepo.getListOrder(params.keyword)
    }

    data class Params(val keyword: String)
}