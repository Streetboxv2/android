package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.SynchronousUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.entities.None
import com.zeepos.models.master.User
import javax.inject.Inject

/**
 * Created by Arif S. on 5/18/20
 */
class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : SynchronousUseCase<User?, None>{
    override fun execute(params: None): User? {
        return userRepository.getCurrentUser()
    }
}