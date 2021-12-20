package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.RemoteRepository
import io.reactivex.Completable
import javax.inject.Inject

class SendTokenFoodtruckUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : CompletableUseCase<SendTokenFoodtruckUseCase.Params>() {

    override fun buildUseCaseCompletable(params: Params): Completable {
        return remoteRepository.sendDataTokenFoodtruck(params)
    }

    data class Params(
        val token: String
    )
}
