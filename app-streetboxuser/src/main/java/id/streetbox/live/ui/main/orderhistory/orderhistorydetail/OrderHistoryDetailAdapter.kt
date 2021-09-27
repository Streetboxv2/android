package id.streetbox.live.ui.main.orderhistory.orderhistorydetail

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.entities.OrderDetail
import id.streetbox.live.R

/**
 * Created by Arif S. on 8/11/20
 */
class OrderHistoryDetailAdapter(data: MutableList<OrderDetail> = mutableListOf()) :
    BaseQuickAdapter<OrderDetail, BaseViewHolder>(R.layout.item_order_history_detail, data) {
    override fun convert(holder: BaseViewHolder, item: OrderDetail) {
        holder.setText(R.id.tv_item_name, item.productName)
        holder.setText(R.id.tv_qty, "${item.qty}")
    }
}