package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Check
import com.zeepos.models.master.Shift
import io.reactivex.Single
import javax.inject.Inject

class CheckUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : SingleUseCase<ResponseApi<Check>, CheckUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<ResponseApi<Check>> {
        return taskOperatorRepo.checkIn(params)
    }

    data class Params(val latitude: Double, val longitude: Double,val parkingSpaceName: String, val tasksId: Long, val typesId: Long)

}