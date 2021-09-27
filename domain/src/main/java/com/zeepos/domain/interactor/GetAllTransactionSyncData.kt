package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.transaction.Order
import io.reactivex.Single
import javax.inject.Inject

class GetAllTransactionSyncData @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : SingleUseCase<List<Order>, GetAllTransactionSyncData.Params>() {

    data class Params(val startDate: Long, val endDate: Long, val keyword: String)

    override fun buildUseCaseSingle(params: Params): Single<List<Order>> {
        return syncDataRepo.getAllTransaction(params.startDate, params.endDate, params.keyword)
    }

}