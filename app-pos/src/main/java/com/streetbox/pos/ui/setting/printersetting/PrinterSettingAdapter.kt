package com.streetbox.pos.ui.setting.printersetting

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.master.PrinterSetting

/**
 * Created by Arif S. on 7/20/20
 */
class PrinterSettingAdapter(data: MutableList<PrinterSetting> = mutableListOf()) :
    BaseQuickAdapter<PrinterSetting, BaseViewHolder>(R.layout.item_printer_setting, data) {
    override fun convert(holder: BaseViewHolder, item: PrinterSetting) {
        holder.setText(R.id.tv_title, item.name)
        holder.setImageResource(
            R.id.iv_icon,
            R.drawable.ic_lock
        )
    }
}