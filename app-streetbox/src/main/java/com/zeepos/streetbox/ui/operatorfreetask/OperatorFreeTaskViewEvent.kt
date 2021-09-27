package com.zeepos.streetbox.ui.operatorfreetask

import com.zeepos.models.master.Check
import com.zeepos.models.master.ShiftOut
import com.zeepos.models.master.StatusNonRegular
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.ui_base.ui.BaseViewEvent

sealed class OperatorFreeTaskViewEvent : BaseViewEvent {
    data class GetAllParkingOperatorFreeTaskSuccess(val data: TaskOperator) :
        OperatorFreeTaskViewEvent()

    data class GetAllParkingOperatorFreeTaskFailed(val errorMessage: String) :
        OperatorFreeTaskViewEvent()

    data class GetStatusNonRegulerSuccess(val data: StatusNonRegular) : OperatorFreeTaskViewEvent()
    data class GetStatusNonRegulerFailed(val message: String?) : OperatorFreeTaskViewEvent()
    data class GetTaskOnGoingSuccess(val data: String) : OperatorFreeTaskViewEvent()
    data class GetTaskOnGoingFailed(val message: String?) : OperatorFreeTaskViewEvent()
    data class GetCheckOutFreeTaskSuccess(val data: Check) : OperatorFreeTaskViewEvent()
    data class GetCheckOutFreeTaskFailed(val message: String?) : OperatorFreeTaskViewEvent()
    data class GetCreateFreeTaskSuccess(val data: Check) : OperatorFreeTaskViewEvent()
    data class GetCreateFreeTaskFailed(val message: String?) : OperatorFreeTaskViewEvent()
    data class GetShiftOutSuccess(val data: ShiftOut) : OperatorFreeTaskViewEvent()
    data class GetShiftOutFailed(val message: String?) : OperatorFreeTaskViewEvent()
    data class GetCheckInFreeTaskSuccess(val data: Check) : OperatorFreeTaskViewEvent()
    data class GetCheckInFreeTaskFailed(val message: String?) : OperatorFreeTaskViewEvent()
}