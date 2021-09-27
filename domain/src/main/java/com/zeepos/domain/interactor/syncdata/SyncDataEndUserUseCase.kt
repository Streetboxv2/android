package com.zeepos.domain.interactor.syncdata

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.SyncDataRepo
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 7/8/20
 */
class SyncDataEndUserUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : CompletableUseCase<SyncDataEndUserUseCase.Params>() {
    override fun buildUseCaseCompletable(params: Params): Completable {
        return syncDataRepo.syncTransactionDataEndUser(params.orderUniqueId)
    }

    data class Params(val orderUniqueId: String)
}