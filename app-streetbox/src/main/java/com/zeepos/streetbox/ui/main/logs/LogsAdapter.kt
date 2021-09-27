package com.zeepos.streetbox.ui.main.logs

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.entities.Logs
import com.zeepos.streetbox.R
import com.zeepos.utilities.DateTimeUtil

/**
 * Created by Arif S. on 7/1/20
 */
class LogsAdapter(data: MutableList<Logs> = mutableListOf()) :
    BaseQuickAdapter<Logs, BaseViewHolder>(
        R.layout.item_logs, data
    ), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: Logs) {

        val dateTimeStr = item.time ?: System.currentTimeMillis().toString()
        val dateTime = DateTimeUtil.getDateWithFormat(dateTimeStr, "dd MMM yyyy hh:mm")

        holder.setText(R.id.tv_date, dateTime)
        holder.setText(R.id.tv_content, item.activity)
    }
}