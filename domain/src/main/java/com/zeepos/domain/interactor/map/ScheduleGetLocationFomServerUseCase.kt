package com.zeepos.domain.interactor.map

import com.zeepos.domain.interactor.base.ObservableUseCase
import com.zeepos.domain.repository.MapRepo
import com.zeepos.models.entities.MapData
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Arif S. on 6/27/20
 */
class ScheduleGetLocationFomServerUseCase @Inject constructor(
    private val mapRepo: MapRepo
) : ObservableUseCase<MapData, ScheduleGetLocationFomServerUseCase.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<MapData> {
        return mapRepo.scheduleGetLocationFromServer(params.taskId)
    }

    data class Params(val taskId: Long)
}