package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Check
import io.reactivex.Single
import javax.inject.Inject

class CheckOutFreeTaskUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : SingleUseCase<ResponseApi<Check>, CheckOutFreeTaskUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<ResponseApi<Check>> {
        return taskOperatorRepo.checkOutFreeTask(params)
    }

    data class Params(val address: String, val latitude: Double, val longitude: Double, val tasksId:Long)

}