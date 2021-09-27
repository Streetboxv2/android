package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.User
import io.reactivex.Single
import javax.inject.Inject

class GetMerchantInfoUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : SingleUseCase<ResponseApi<User>, None>() {
    override fun buildUseCaseSingle(params: None): Single<ResponseApi<User>> {
        return syncDataRepo.getMerchantInfo()
    }
}