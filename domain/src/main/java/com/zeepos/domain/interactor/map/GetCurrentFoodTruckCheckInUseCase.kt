package com.zeepos.domain.interactor.map

import com.zeepos.domain.interactor.base.ObservableUseCase
import com.zeepos.domain.repository.MapRepo
import com.zeepos.models.entities.MapData
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Arif S. on 7/4/20
 */
class GetCurrentFoodTruckCheckInUseCase @Inject constructor(
    private val mapRepo: MapRepo
) : ObservableUseCase<MapData, GetCurrentFoodTruckCheckInUseCase.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<MapData> {
        return mapRepo.getCheckInLocationFromServer(params.taskId)
    }

    data class Params(val taskId: Long)
}