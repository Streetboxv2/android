package com.zeepos.domain.interactor.parkingspace

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.models.master.ParkingSpace
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Arif S. on 5/23/20
 */
class SearchParkingSpaceUseCase @Inject constructor(
    private val parkingSpaceRepository: ParkingSpaceRepository
) : MaybeUseCase<List<ParkingSpace>, SearchParkingSpaceUseCase.Params>() {

    override fun buildUseCaseMaybe(params: Params): Maybe<List<ParkingSpace>> {
        return parkingSpaceRepository.searchParkingSpace(params.keyword)
    }

    data class Params(val keyword: String)
}