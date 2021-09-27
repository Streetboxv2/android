package com.zeepos.domain.interactor.address

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.AddressRepo
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 8/26/20
 */
class DeleteAddressUseCase @Inject constructor(
    private val addressRepo: AddressRepo
) :
    CompletableUseCase<DeleteAddressUseCase.Params>() {
    data class Params(val id: Long)

    override fun buildUseCaseCompletable(params: Params): Completable {
        return addressRepo.delete(params.id)
    }
}