package com.zeepos.domain.interactor.map

import com.zeepos.domain.interactor.base.ObservableUseCase
import com.zeepos.domain.repository.MapRepo
import com.zeepos.models.entities.MapData
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Arif S. on 5/25/20
 */
class RequestLiveTrackingUseCase @Inject constructor(
    private val mapRepo: MapRepo
) : ObservableUseCase<MapData, RequestLiveTrackingUseCase.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<MapData> {
        return mapRepo.requestMapTrackingDirectionOperator(params)
    }

    data class Params(val taskId: Long, val latitude: Double, val longitude: Double)
}