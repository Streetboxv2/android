package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.None
import io.reactivex.Completable
import javax.inject.Inject

class GetReceiveMessage @Inject constructor(
    private val remoteRepository: RemoteRepository
) : CompletableUseCase<None>() {

    override fun buildUseCaseCompletable(params:None): Completable {
        return remoteRepository.getReceiveMessage()
    }

}