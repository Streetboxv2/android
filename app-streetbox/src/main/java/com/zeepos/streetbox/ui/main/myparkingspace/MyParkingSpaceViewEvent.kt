package com.zeepos.streetbox.ui.main.myparkingspace

import com.zeepos.models.master.User
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.streetbox.ui.operatormerchant.OperatorViewEvent
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/21/20
 */
sealed class MyParkingSpaceViewEvent : BaseViewEvent {
    data class GetAllParkingSalesSuccess(val data: List<ParkingSales>) :
        MyParkingSpaceViewEvent()

    data class GetOperatorSuccess(val data: List<User>?) : MyParkingSpaceViewEvent()
    data class GetOperatorFailed(val message: String?) : MyParkingSpaceViewEvent()

    data class GetAllParkingSpaceFailed(val errorMessage: String) : MyParkingSpaceViewEvent()

    data class GetTaskOperatorSuccess(val data: List<TaskOperator>) : MyParkingSpaceViewEvent()

    object GetTaskOperatorFailed : MyParkingSpaceViewEvent()
}