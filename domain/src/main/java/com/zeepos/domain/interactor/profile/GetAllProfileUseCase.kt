package com.zeepos.domain.interactor.profile

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.User
import io.reactivex.Single
import javax.inject.Inject

class GetAllProfileUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<ResponseApi<User>, None>() {
    override fun buildUseCaseSingle(params: None): Single<ResponseApi<User>> {
        return remoteRepository.getProfile()
    }
}