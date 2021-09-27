package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.UserAuthData
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/10/20
 */
class LoginPosUseCase @Inject constructor(
    private val userRepository: UserRepository
) : SingleUseCase<UserAuthData, LoginPosUseCase.Params>() {

    data class Params(val username: String, val password: String)

    override fun buildUseCaseSingle(params: Params): Single<UserAuthData> {
        return userRepository.loginPOS(params)
    }
}