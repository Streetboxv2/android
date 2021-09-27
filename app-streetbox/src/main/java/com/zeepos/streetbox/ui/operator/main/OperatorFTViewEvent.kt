package com.zeepos.streetbox.ui.operator.main

import com.zeepos.models.master.Shift
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewEvent

sealed class OperatorFTViewEvent : BaseViewEvent {
    data class GetTaskOperatorSuccess(val data: List<User>?) : OperatorFTViewEvent()
    data class GetTaskOperatorFailed(val message: String?) : OperatorFTViewEvent()
    data class GetShiftSuccess(val data: Shift) : OperatorFTViewEvent()
    data class GetShiftFailed(val message: String?) : OperatorFTViewEvent()
    data class CheckStatusShiftIn(val message: Shift) : OperatorFTViewEvent()
    data class CheckStatusShiftFailed(val message: String?) : OperatorFTViewEvent()
    object ShiftFalse : OperatorFTViewEvent()
    object ShiftTrue : OperatorFTViewEvent()
}