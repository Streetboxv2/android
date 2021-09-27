package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.transaction.TaskOperator
import io.reactivex.Single
import javax.inject.Inject

class GetListFreeTaskUseCase @Inject constructor(
    private val operatorRepo: TaskOperatorRepo
) : SingleUseCase<ResponseApi<TaskOperator>, None>() {
    override fun buildUseCaseSingle(params: None): Single<ResponseApi<TaskOperator>> {
        return operatorRepo.getFreeTask()
    }

}