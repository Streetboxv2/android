package com.zeepos.domain.interactor.syncdata

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.SyncDataRepo
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 7/30/20
 */
class SyncDataPOSUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : CompletableUseCase<SyncDataPOSUseCase.Params>() {
    override fun buildUseCaseCompletable(params: Params): Completable {
        return syncDataRepo.syncTransactionDataPOS(params.syncDataUniqueId)
    }

    data class Params(val syncDataUniqueId: String)
}