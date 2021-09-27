package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.ShiftOut
import io.reactivex.Single
import javax.inject.Inject

class ShiftOutUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<ResponseApi<ShiftOut>, None>() {

    override fun buildUseCaseSingle(params: None): Single<ResponseApi<ShiftOut>> {
        return remoteRepository.shiftOut(params)
    }

}