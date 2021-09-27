package com.zeepos.domain.interactor.registration

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.RegisterParams
import com.zeepos.models.master.UserAuthData
import io.reactivex.Single
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<UserAuthData, RegistrationUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<UserAuthData> {
        return remoteRepository.registration(params.registerParams)
    }

    data class Params(val registerParams: RegisterParams)
}