package com.zeepos.domain.interactor.operator.task

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.CreateTask
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 5/22/20
 */
class CreateTaskOperatorUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : CompletableUseCase<CreateTaskOperatorUseCase.Params>() {

    override fun buildUseCaseCompletable(params: Params): Completable {
        return taskOperatorRepo.createTaskOperator(params)
    }

    data class Params(val createTask: CreateTask)
}