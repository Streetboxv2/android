package com.zeepos.domain.interactor.syncdata

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.entities.None
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 8/11/20
 */
class SyncDataHomeVisitUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : SingleUseCase<None, SyncDataHomeVisitUseCase.Params>() {
    data class Params(val data: String)

    override fun buildUseCaseSingle(params: Params): Single<None> {
        return syncDataRepo.syncTransactionDataHomeVisit(params.data)
    }

}