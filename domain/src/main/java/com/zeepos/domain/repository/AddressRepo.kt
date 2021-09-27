package com.zeepos.domain.repository

import com.zeepos.models.master.Address
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Arif S. on 8/26/20
 */
interface AddressRepo {
    fun getAll(): Single<List<Address>>
    fun save(address: Address): Completable
    fun delete(id: Long): Completable
}