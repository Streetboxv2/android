package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Check
import com.zeepos.models.master.Shift
import io.reactivex.Single
import javax.inject.Inject

class CreateFreeTaskUseCase @Inject constructor(
    private val taskOperatorRepo: TaskOperatorRepo
) : SingleUseCase<ResponseApi<Check>, None>() {

    override fun buildUseCaseSingle(params: None): Single<ResponseApi<Check>> {
        return taskOperatorRepo.createFreeTask(params)
    }

}