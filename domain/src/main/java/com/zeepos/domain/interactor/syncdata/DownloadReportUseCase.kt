package com.zeepos.domain.interactor.syncdata

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.SyncDataRepo
import io.reactivex.Single
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Created by Arif S. on 8/17/20
 */
class DownloadReportUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) :
    SingleUseCase<ResponseBody, DownloadReportUseCase.Params>() {
    data class Params(val month: String, val year: String, val url: String)

    override fun buildUseCaseSingle(params: Params): Single<ResponseBody> {
        return syncDataRepo.downloadReport(params.month, params.year, params.url)
    }
}