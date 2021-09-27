package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.interactor.operator.task.CreateTaskOperatorUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.CreateHomeVisitTask
import com.zeepos.models.entities.CreateTask
import io.reactivex.Completable
import javax.inject.Inject

class CreateOperatorHomeVisitUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : CompletableUseCase<CreateOperatorHomeVisitUseCase.Params>() {

    override fun buildUseCaseCompletable(params: Params): Completable {
        return taskOperatorRepo.createTaskOperatorHomeVisit(params)
    }

    data class Params(val trxVisitSalesId: Long, val usersId: Long)

}