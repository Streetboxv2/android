package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Check
import io.reactivex.Single
import javax.inject.Inject

class CheckOutHomeVisitUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : SingleUseCase<ResponseApi<Check>, CheckOutHomeVisitUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<ResponseApi<Check>> {
        return taskOperatorRepo.checkOutHomeVisit(params)
    }

    data class Params(val customerName: String, val latitude: Double, val longitude: Double, val tasksId:Long, val typesId: Long)

}