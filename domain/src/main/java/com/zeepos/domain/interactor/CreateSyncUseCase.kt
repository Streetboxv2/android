package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.SyncDataRepo
import io.reactivex.Completable
import javax.inject.Inject

class CreateSyncUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : CompletableUseCase<CreateSyncUseCase.Params>() {

    override fun buildUseCaseCompletable(params: Params): Completable {
        return syncDataRepo.createSync(params)
    }

    data class Params(
        val type: String,
        val businessDate: Long,
        val data: String
    )
}