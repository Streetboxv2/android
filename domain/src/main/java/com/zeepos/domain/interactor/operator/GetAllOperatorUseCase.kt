package com.zeepos.domain.interactor.operator

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.entities.None
import com.zeepos.models.master.User
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Arif S. on 6/8/20
 */
class GetAllOperatorUseCase @Inject constructor(
    private val userRepository: UserRepository
) : MaybeUseCase<List<User>, None>() {
    override fun buildUseCaseMaybe(params: None): Maybe<List<User>> {
        return userRepository.getAllOperatorCloud()
    }
}