package com.zeepos.domain.interactor.logs

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.LogsRepo
import com.zeepos.models.entities.Logs
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/1/20
 */
class LogsUseCase @Inject constructor(
    private val logsRepo: LogsRepo
) : SingleUseCase<List<Logs>, LogsUseCase.Params>() {
    override fun buildUseCaseSingle(params: Params): Single<List<Logs>> {
        return logsRepo.getLogs(params.isLoadMore)
    }

    data class Params(val isLoadMore: Boolean = false)
}