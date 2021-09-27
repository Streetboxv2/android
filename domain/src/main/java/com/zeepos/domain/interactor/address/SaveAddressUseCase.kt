package com.zeepos.domain.interactor.address

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.AddressRepo
import com.zeepos.models.master.Address
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 8/26/20
 */
class SaveAddressUseCase @Inject constructor(
    private val addressRepo: AddressRepo
) : CompletableUseCase<SaveAddressUseCase.Params>() {
    data class Params(val address: Address)

    override fun buildUseCaseCompletable(params: Params): Completable {
        return addressRepo.save(params.address)
    }
}