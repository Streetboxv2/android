package com.zeepos.domain.interactor.map

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.MapRepo
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 6/4/20
 */
class UpdateCurrentLocationToCloud @Inject constructor(
    private val mapRepo: MapRepo
) : CompletableUseCase<UpdateCurrentLocationToCloud.Params>() {

    override fun buildUseCaseCompletable(params: Params): Completable {
        return mapRepo.updateLocationToCloud(params)
    }

    data class Params(
        val taskId: Long,
        val currentLat: Double,
        val currentLng: Double,
        val status: Int
    )
}