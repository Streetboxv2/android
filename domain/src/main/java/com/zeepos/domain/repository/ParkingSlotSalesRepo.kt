package com.zeepos.domain.repository

import com.zeepos.models.transaction.ParkingSlotSales

/**
 * Created by Arif S. on 5/19/20
 */
interface ParkingSlotSalesRepo {
    fun get(): List<ParkingSlotSales>
    fun get(id: Long): ParkingSlotSales?
    fun insertUpdate(parkingSlotSales: ParkingSlotSales)
    fun insertUpdate(parkingSlotSalesList: List<ParkingSlotSales>)
}