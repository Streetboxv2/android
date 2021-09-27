package com.zeepos.domain.interactor

import android.icu.lang.UCharacter
import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.ResponseApi
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


class CloseOnlineOrderUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : CompletableUseCase<CloseOnlineOrderUseCase.Params>() {

    override fun buildUseCaseCompletable(params: Params): Completable {
        return syncDataRepo.closeOnlineOrder(params)
    }
    data class Params(
        val trxId :String
    )
}
