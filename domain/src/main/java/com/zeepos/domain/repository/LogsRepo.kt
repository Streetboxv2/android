package com.zeepos.domain.repository

import com.zeepos.models.entities.Logs
import io.reactivex.Single

/**
 * Created by Arif S. on 7/1/20
 */
interface LogsRepo {
    fun getLogs(isLoadMore: Boolean): Single<List<Logs>>
}