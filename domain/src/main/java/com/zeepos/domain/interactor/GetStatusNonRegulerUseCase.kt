package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Shift
import com.zeepos.models.master.StatusNonRegular
import io.reactivex.Single
import javax.inject.Inject

class GetStatusNonRegulerUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<ResponseApi<StatusNonRegular>, None>() {

    override fun buildUseCaseSingle(params: None): Single<ResponseApi<StatusNonRegular>> {
        return remoteRepository.getSatusNonReguler()
    }

}