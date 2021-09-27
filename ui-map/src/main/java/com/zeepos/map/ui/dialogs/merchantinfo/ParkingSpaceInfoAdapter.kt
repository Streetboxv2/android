package com.zeepos.map.ui.dialogs.merchantinfo

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.map.R
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.DateTimeUtil

/**
 * Created by Arif S. on 8/18/20
 */
class ParkingSpaceInfoAdapter(data: MutableList<ParkingSchedule> = mutableListOf()) :
    BaseQuickAdapter<ParkingSchedule, BaseViewHolder>(
        R.layout.item_parking_schedule_info, data
    ) {
    override fun convert(holder: BaseViewHolder, item: ParkingSchedule) {
        val scheduleTime = if (item.schedules.isNotEmpty()) item.schedules[0] else null
        val startTimeStr = scheduleTime?.startDate ?: "0"
        val endTimeStr = scheduleTime?.endDate ?: "0"
        val startTime = DateTimeUtil.getDateWithFormat1(startTimeStr, "EEEE HH:mm")
        val endTime = DateTimeUtil.getDateWithFormat1(endTimeStr, "HH:mm")

        holder.setText(R.id.tv_title, item.merchantName)
        holder.setText(R.id.tv_schedule, "$startTime - $endTime")

        val ivLogo = holder.getView<ImageView>(R.id.iv_merchant_logo)

        val imageUrl = ConstVar.PATH_IMAGE + item.logo

        GlideApp.with(context)
            .load(imageUrl)
            .into(ivLogo)
    }
}