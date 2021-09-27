package com.streetbox.pos.ui.setting.printersetting

import com.zeepos.models.master.PrinterSetting
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 7/20/20
 */
class PrinterSettingViewModel @Inject constructor() : BaseViewModel<PrinterSettingViewEvent>() {

    fun getAllPrinter() {
        val printerList: MutableList<PrinterSetting> = mutableListOf()
        for (i in 1..3) {
            val printerSetting = PrinterSetting()
            printerSetting.name = "Printer $i"
            printerSetting.attribute = "Bluetooth"
            printerList.add(printerSetting)
        }

        viewEventObservable.postValue(PrinterSettingViewEvent.GetAllPrinterSuccess(printerList))
    }
}