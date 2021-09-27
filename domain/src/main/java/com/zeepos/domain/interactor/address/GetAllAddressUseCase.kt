package com.zeepos.domain.interactor.address

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.AddressRepo
import com.zeepos.models.entities.None
import com.zeepos.models.master.Address
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 8/26/20
 */
class GetAllAddressUseCase @Inject constructor(
    private val addressRepo: AddressRepo
) : SingleUseCase<List<Address>, None>() {
    override fun buildUseCaseSingle(params: None): Single<List<Address>> {
        return addressRepo.getAll()
    }
}