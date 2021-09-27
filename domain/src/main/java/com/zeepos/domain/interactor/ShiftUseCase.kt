package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Shift
import io.reactivex.Single
import javax.inject.Inject

class ShiftUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<ResponseApi<Shift>, None>() {

    override fun buildUseCaseSingle(params: None): Single<ResponseApi<Shift>> {
        return remoteRepository.shiftIn(params)
    }

}