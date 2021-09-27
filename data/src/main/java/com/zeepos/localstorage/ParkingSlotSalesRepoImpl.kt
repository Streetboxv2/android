package com.zeepos.localstorage

import com.zeepos.domain.repository.ParkingSlotSalesRepo
import com.zeepos.models.transaction.ParkingSlotSales
import io.objectbox.Box
import io.objectbox.BoxStore
import javax.inject.Inject

/**
 * Created by Arif S. on 5/19/20
 */
class ParkingSlotSalesRepoImpl @Inject constructor(
    boxStore: BoxStore
) : ParkingSlotSalesRepo {
    private val box: Box<ParkingSlotSales> = boxStore.boxFor(
        ParkingSlotSales::class.java
    )

    override fun get(): List<ParkingSlotSales> {
        return box.all
    }

    override fun get(id: Long): ParkingSlotSales? {
        return box.get(id)
    }

    override fun insertUpdate(parkingSlotSales: ParkingSlotSales) {
        box.put(parkingSlotSales)
    }

    override fun insertUpdate(parkingSlotSalesList: List<ParkingSlotSales>) {
        box.put(parkingSlotSalesList)
    }
}