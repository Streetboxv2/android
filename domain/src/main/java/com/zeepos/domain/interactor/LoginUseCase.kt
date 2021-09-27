package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.master.User
import com.zeepos.models.master.UserAuthData
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 5/7/20
 */
class LoginUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<UserAuthData, LoginUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<UserAuthData> {
        return remoteRepository.login(params)
    }

    data class Params(val username: String, val password: String)

}