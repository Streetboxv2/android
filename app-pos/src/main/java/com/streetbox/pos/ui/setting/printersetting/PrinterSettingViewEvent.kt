package com.streetbox.pos.ui.setting.printersetting

import com.zeepos.models.master.PrinterSetting
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 7/20/20
 */
sealed class PrinterSettingViewEvent : BaseViewEvent {
    data class GetAllPrinterSuccess(val data: List<PrinterSetting>) : PrinterSettingViewEvent()
}