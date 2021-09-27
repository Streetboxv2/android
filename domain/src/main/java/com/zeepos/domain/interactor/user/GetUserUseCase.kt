package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.User
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 5/18/20
 */
class GetUserUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<ResponseApi<User>, GetUserUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<ResponseApi<User>> {
        return remoteRepository.getUser(params.userId)
    }

    data class Params(val userId: Long)
}