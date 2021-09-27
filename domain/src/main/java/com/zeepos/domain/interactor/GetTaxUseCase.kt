package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.master.Tax
import io.reactivex.Single
import javax.inject.Inject

class GetTaxUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : SingleUseCase<Tax, GetTaxUseCase.Params>() {
    override fun buildUseCaseSingle(params: Params): Single<Tax> {
        return syncDataRepo.getTaxSetting(params.merchantId)
    }

    data class Params(val merchantId: Long)

}