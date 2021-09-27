package com.zeepos.domain.interactor.operator.task

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.transaction.TaskOperator
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Arif S. on 6/3/20
 */
class GetTaskOperatorByParkingSalesUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : MaybeUseCase<List<TaskOperator>, GetTaskOperatorByParkingSalesUseCase.Params>(){

    override fun buildUseCaseMaybe(params: Params): Maybe<List<TaskOperator>> {
        return taskOperatorRepo.getByParkingSalesIdCloud(params.parkingSalesId)
    }

    data class Params(val parkingSalesId : Long)
}