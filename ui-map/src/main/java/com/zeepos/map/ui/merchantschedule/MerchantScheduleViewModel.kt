package com.zeepos.map.ui.merchantschedule

import com.zeepos.domain.interactor.parkingspace.GetParkingSpaceScheduleUseCase
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 8/12/20
 */
class MerchantScheduleViewModel @Inject constructor(
    private val getParkingSpaceScheduleUseCase: GetParkingSpaceScheduleUseCase
) :
    BaseViewModel<MerchantScheduleViewEvent>() {

}