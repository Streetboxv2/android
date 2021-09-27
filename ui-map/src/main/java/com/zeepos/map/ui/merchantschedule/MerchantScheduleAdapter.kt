package com.zeepos.map.ui.merchantschedule

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.map.R
import com.zeepos.models.entities.Schedule
import com.zeepos.utilities.DateTimeUtil

/**
 * Created by Arif S. on 8/12/20
 */
class MerchantScheduleAdapter(data: MutableList<Schedule> = mutableListOf()) :
    BaseQuickAdapter<Schedule, BaseViewHolder>(
        R.layout.item_merchant_info, data
    ) {
    override fun convert(holder: BaseViewHolder, item: Schedule) {
        val startScheduleDate = DateTimeUtil.getDateWithFormat1(item.startDate!!, "dd MMM")
        val endScheduleDate = DateTimeUtil.getDateWithFormat1(item.endDate!!, "dd MMM")
        val startScheduleTime = DateTimeUtil.getDateWithFormat1(item.startDate!!, "HH:mm")
        val endScheduleTime = DateTimeUtil.getDateWithFormat1(item.endDate!!, "HH:mm")

        holder.setText(R.id.tv_schedule, "$startScheduleDate - $endScheduleDate")
        holder.setText(R.id.tv_schedule_time, "$startScheduleTime - $endScheduleTime")
    }
}