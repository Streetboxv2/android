package com.zeepos.domain.repository

import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.models.entities.Schedule
import com.zeepos.models.master.ParkingSpace
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by Arif S. on 5/15/20
 */
interface ParkingSpaceRepository {
    fun insertUpdateParkingSpace(parkingSpaceList: List<ParkingSpace>)
    fun getParkingSpace(): Single<List<ParkingSpace>>
    fun getParkingSpace(id: Long): ParkingSpace?
    fun attach(parkingSpace: ParkingSpace)
    fun searchParkingSpace(keyword: String): Maybe<List<ParkingSpace>>
    fun getParkingSpaceCloud(isRefresh: Boolean): Single<List<ParkingSpace>>
    fun getParkingSchedule(parkingSpaceId: Long): Single<List<ParkingSchedule>>
    fun getMerchantParkingSchedule(merchantId: Long, typesId: Long): Single<List<Schedule>>
}