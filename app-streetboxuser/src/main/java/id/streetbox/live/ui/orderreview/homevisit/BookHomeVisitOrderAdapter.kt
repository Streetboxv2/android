package id.streetbox.live.ui.orderreview.homevisit

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.entities.VisitSales
import id.streetbox.live.R
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil

/**
 * Created by Arif S. on 8/10/20
 */
class BookHomeVisitOrderAdapter(data: MutableList<VisitSales> = mutableListOf()) :
    BaseQuickAdapter<VisitSales, BaseViewHolder>(
        R.layout.item_book_home_visit_order, data
    ) {
    override fun convert(holder: BaseViewHolder, item: VisitSales) {
        val scheduled = "${DateTimeUtil.getDateWithFormat(item.scheduleDate, "dd MMM yyyy")} " +
                "${DateTimeUtil.getDateWithFormat(item.startTime, "HH:mm")} - " +
                "${DateTimeUtil.getDateWithFormat(item.endTime, "HH:mm")} "
        val price = "${NumberUtil.formatToStringWithoutDecimal(item.deposit.toDouble())} "

        holder.setText(R.id.tv_schedule_date_time, scheduled)
        holder.setText(R.id.tv_price, price)
    }
}