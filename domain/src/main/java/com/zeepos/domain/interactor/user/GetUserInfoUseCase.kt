package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 8/13/20
 */
class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) : SingleUseCase<User, GetUserInfoUseCase.Params>() {
    data class Params(val userId: Long)

    override fun buildUseCaseSingle(params: Params): Single<User> {
        return userRepository.getUserCloud(params.userId)
    }

}