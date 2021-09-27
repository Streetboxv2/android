package com.zeepos.domain.interactor.parkingsales

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.None
import com.zeepos.models.transaction.TaskOperator
import io.reactivex.Single
import javax.inject.Inject

class GetParkingOperatorTaskUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : SingleUseCase<List<TaskOperator>, None>() {
    override fun buildUseCaseSingle(params: None): Single<List<TaskOperator>> {
        return taskOperatorRepo.getMyTask()
    }

}