package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.RemoteRepository
import io.reactivex.Completable
import javax.inject.Inject

class SendTokenUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : CompletableUseCase<SendTokenUseCase.Params>() {

    override fun buildUseCaseCompletable(params: Params): Completable {
        return remoteRepository.sendDataToken(params)
    }

    data class Params(
        val token: String
    )
}
