package com.zeepos.localstorage

import com.zeepos.domain.repository.AddressRepo
import com.zeepos.models.master.Address
import com.zeepos.models.master.Address_
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 8/26/20
 */
class AddressRepoImpl @Inject internal constructor(
    boxStore: BoxStore
) : AddressRepo {

    private val box: Box<Address> by lazy {
        boxStore.boxFor(
            Address::class.java
        )
    }

    override fun getAll(): Single<List<Address>> {
        return Single.fromCallable {
            box.query().orderDesc(Address_.createdAt).build().find()
        }
    }

    override fun save(address: Address): Completable {
        return Completable.fromCallable {
            val addressList = box.query().orderDesc(Address_.createdAt).build().find()
            var pos = 0
            addressList.forEach {
                if (it.address == address.address) {
                    address.id = it.id
                    return@forEach
                } else {
                    if (pos == 1) {
                        box.remove(it)
                    }
                }

                pos = pos.inc()
            }

            box.put(address)
        }
    }

    override fun delete(id: Long): Completable {
        return Completable.fromCallable { box.remove(id) }
    }
}