package com.zeepos.domain.interactor.transaction

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.UserRepository
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 5/19/20
 */
class IncreaseSlotQuantityUseCase @Inject constructor(
    private val userRepository: UserRepository
) : CompletableUseCase<IncreaseSlotQuantityUseCase.Params>() {

    override fun buildUseCaseCompletable(params: Params): Completable {
        TODO("Not yet implemented")
    }

    data class Params(val slotId: Long)

}