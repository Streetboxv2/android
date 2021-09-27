package com.zeepos.domain.interactor.map

import com.zeepos.domain.interactor.base.ObservableUseCase
import com.zeepos.domain.repository.MapRepo
import com.zeepos.models.entities.MapData
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Arif S. on 5/27/20
 */
class ScheduleGetAllLocationFromServerUseCase @Inject constructor(private val mapRepo: MapRepo) :
    ObservableUseCase<MapData, ScheduleGetAllLocationFromServerUseCase.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<MapData> {
        return mapRepo.scheduleGetAllLocationFromServer(params.latitude, params.longitude)
    }

    data class Params(val latitude: Double, val longitude: Double)
}
