package com.zeepos.streetbox.ui.main.logs

import com.zeepos.models.entities.Logs
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 7/1/20
 */
sealed class LogViewEvent : BaseViewEvent {
    data class GetLogSuccess(val data: List<Logs>) : LogViewEvent()
    object GetLogsFailed : LogViewEvent()
}