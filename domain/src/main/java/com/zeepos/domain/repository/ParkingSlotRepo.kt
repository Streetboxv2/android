package com.zeepos.domain.repository

import com.zeepos.models.master.ParkingSlot
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by Arif S. on 5/18/20
 */
interface ParkingSlotRepo {
    fun insertUpdate(data: ParkingSlot)
    fun insertUpdate(data: List<ParkingSlot>)
    fun getByParkingSpaceId(parkingSpaceId: Long): List<ParkingSlot>
    fun get(id: Long): ParkingSlot?
    fun getWithObs(id: Long): Maybe<ParkingSlot>
    fun getParkingDetail(parkingSpaceId: Long): Single<List<ParkingSlot>>
}