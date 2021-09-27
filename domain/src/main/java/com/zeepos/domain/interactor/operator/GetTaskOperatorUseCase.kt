package com.zeepos.domain.interactor.operator

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import io.reactivex.Maybe
import javax.inject.Inject

class GetTaskOperatorUseCase @Inject constructor(
    private val userRepository: UserRepository
) : MaybeUseCase<List<User>, GetTaskOperatorUseCase.Params>() {

    override fun buildUseCaseMaybe(params: Params): Maybe<List<User>> {
        return userRepository.getAllAvailableOperator()
    }

    data class Params(val parkingSlotId: Long)
}