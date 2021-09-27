package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Check
import io.reactivex.Single
import javax.inject.Inject

class UndoUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : SingleUseCase<ResponseApi<String>, UndoUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<ResponseApi<String>> {
        return taskOperatorRepo.undo(params.tasksId)
    }

    data class Params(val tasksId: Long)
}