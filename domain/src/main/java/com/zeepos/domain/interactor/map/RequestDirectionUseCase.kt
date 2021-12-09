package com.zeepos.domain.interactor.map

import com.zeepos.domain.interactor.base.ObservableUseCase
import com.zeepos.domain.repository.MapRepo
import com.zeepos.models.entities.MapData
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Arif S. on 5/24/20
 */
class RequestDirectionUseCase @Inject constructor(
    private val mapRepo: MapRepo
) :
    ObservableUseCase<MapData, RequestDirectionUseCase.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<MapData> {
        return mapRepo.getFoodTruckDirection(params)
    }

    data class Params(
        val taskId: Long,
        val currentLat: Double,
        val currentLng: Double,
        val latparkingspace:Double,
        val lonparkingspace:Double
    )
}