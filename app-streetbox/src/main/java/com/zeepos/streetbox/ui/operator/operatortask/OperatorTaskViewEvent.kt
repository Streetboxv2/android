package com.zeepos.streetbox.ui.operator.operatortask

import com.zeepos.models.master.*
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.streetbox.ui.operatorfreetask.OperatorFreeTaskViewEvent

import com.zeepos.ui_base.ui.BaseViewEvent

sealed class OperatorTaskViewEvent : BaseViewEvent {
    data class GetAllParkingOperatorTaskSuccess(val data: MutableList<TaskOperator>) :
        OperatorTaskViewEvent()

    data class GetAllParkingOperatorTaskFailed(val errorMessage: String) : OperatorTaskViewEvent()
    data class GetShiftOutSuccess(val data: ShiftOut) : OperatorTaskViewEvent()
    data class GetShiftOutFailed(val message: String?) : OperatorTaskViewEvent()
    data class GetCheckOutSuccess(val data: Check) : OperatorTaskViewEvent()
    data class GetCheckOutHomeVisitSuccess(val data: Check) : OperatorTaskViewEvent()
    data class GetCheckOutFailed(val message: String?) : OperatorTaskViewEvent()
    data class GetCheckOutHomeVisitFailed(val message: String?) : OperatorTaskViewEvent()
    data class GetTaskOnGoingSuccess(val data: String) : OperatorTaskViewEvent()
    data class GetTaskOnGoingFailed(val message: String?) : OperatorTaskViewEvent()
}