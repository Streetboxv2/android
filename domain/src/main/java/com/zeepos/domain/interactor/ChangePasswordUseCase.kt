package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.User
import io.reactivex.Single
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<ResponseApi<User>, ChangePasswordUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<ResponseApi<User>> {
        return remoteRepository.changePassword(params)
    }

    data class Params( val password: String)

}