package com.zeepos.streetbox.ui.parkingdetail

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.master.ParkingSlot
import com.zeepos.models.transaction.ParkingSlotSales
import com.zeepos.streetbox.R
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil

/**
 * Created by Arif S. on 5/13/20
 */
class ParkingDetailAdapter(data: MutableList<Any> = mutableListOf()) :
    BaseQuickAdapter<Any, BaseViewHolder>(R.layout.parkingspace_detail_item, data) {

    init {
        addChildClickViewIds(R.id.tv_assign)
    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        var schedule: String = ConstVar.EMPTY_STRING
        var scheduleTime: String = ConstVar.EMPTY_STRING

        if (item is ParkingSlot) {
            val availableSlot = item.availableSlot

            val startDateMillis = DateTimeUtil.getDateFromString(item.startDate)?.time ?: 0L
            val endDateMillis = DateTimeUtil.getDateFromString(item.endDate)?.time ?: 0L
            val startDate = DateTimeUtil.getDateWithFormat(startDateMillis, "dd MMM")
            val endDate = DateTimeUtil.getDateWithFormat(endDateMillis, "dd MMM")
            val startTime = DateTimeUtil.getDateWithFormat(startDateMillis, "HH:mm")
            val endTime = DateTimeUtil.getDateWithFormat(endDateMillis, "HH:mm")
            schedule = "$startDate - $endDate"
            scheduleTime = "$startTime - $endTime"
            val point = NumberUtil.formatToStringWithoutDecimal(item.point)

            holder.setText(R.id.tv_slot, "$availableSlot Slots")
            holder.setText(R.id.tv_point_child, point)
            holder.setGone(R.id.tv_assign, true)
        } else if (item is ParkingSlotSales) {
            val availableSlot = item.totalSlot

            val startDateMillis = DateTimeUtil.getDateFromString(item.startDate)?.time ?: 0L
            val endDateMillis = DateTimeUtil.getDateFromString(item.endDate)?.time ?: 0L
            val startDate = DateTimeUtil.getDateWithFormat(startDateMillis, "dd MMM")
            val endDate = DateTimeUtil.getDateWithFormat(endDateMillis, "dd MMM")
            val startTime = DateTimeUtil.getDateWithFormat(startDateMillis, "HH:mm")
            val endTime = DateTimeUtil.getDateWithFormat(endDateMillis, "HH:mm")
            schedule = "$startDate - $endDate"
            scheduleTime = "$startTime - $endTime"

            holder.setText(R.id.tv_slot, "$availableSlot Slots")
            holder.setGone(R.id.tv_point_child, true)
            holder.setVisible(R.id.tv_assign, true)
        }

        holder.setText(R.id.tv_schedule, schedule)
        holder.setText(R.id.tv_schedule_time, scheduleTime)

    }

    companion object {
        const val EXPAND_COLLAPSE_PAYLOAD = 110
    }
}