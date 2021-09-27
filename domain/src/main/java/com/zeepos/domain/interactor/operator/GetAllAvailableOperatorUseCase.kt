package com.zeepos.domain.interactor.operator

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.entities.None
import com.zeepos.models.master.User
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Arif S. on 5/16/20
 */

/**
 * Get all operator yang belum diassign ke task tertentu
 */
class GetAllAvailableOperatorUseCase @Inject constructor(
    private val userRepository: UserRepository
) : MaybeUseCase<List<User>, None>() {

    override fun buildUseCaseMaybe(params: None): Maybe<List<User>> {
        return userRepository.getAllAvailableOperator()
    }

}