package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.transaction.AllTransaction
import com.zeepos.models.transaction.OnlineOrder
import io.reactivex.Single
import javax.inject.Inject

class GetOnlineOrderUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : SingleUseCase<ResponseApi<OnlineOrder>, None>() {
    override fun buildUseCaseSingle(params: None): Single<ResponseApi<OnlineOrder>> {
        return syncDataRepo.getTransactionOnlineOrder()
    }

}