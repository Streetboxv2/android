package com.zeepos.domain.repository

import com.zeepos.domain.interactor.parkingsales.CreateOrUpdateParkingSalesUseCase
import com.zeepos.models.transaction.ParkingSales
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by Arif S. on 5/17/20
 */
interface ParkingSalesRepo {
    fun getParkingSales(): Single<List<ParkingSales>>
    fun getByParkingSpaceId(parkingSpaceId: Long): ParkingSales?
    fun getParkingSalesDetail(id: Long): Single<ParkingSales>
    fun getByParkingSpaceIdOrderUniqueId(parkingSpaceId: Long, orderUniqueId: String): ParkingSales?
    fun insertUpdate(parkingSales: ParkingSales)
    fun createOrUpdateParkingSales(params: CreateOrUpdateParkingSalesUseCase.Params)
    fun insertUpdate(parkingSalesList: List<ParkingSales>)
    fun getParkingSalesCloud(isRefresh: Boolean): Single<List<ParkingSales>>
    fun searchParkingSales(keyword: String): Maybe<List<ParkingSales>>
}