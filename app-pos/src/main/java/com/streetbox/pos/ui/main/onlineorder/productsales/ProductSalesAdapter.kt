package com.streetbox.pos.ui.main.onlineorder.productsales

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.transaction.Order
import com.zeepos.utilities.DateTimeUtil

class ProductSalesAdapter(data: MutableList<Order> = mutableListOf()) :
    BaseQuickAdapter<Order, BaseViewHolder>(R.layout.item_product_sales, data) {

    override fun convert(holder: BaseViewHolder, item: Order) {
        holder.setText(R.id.tv_no, "" + (item.no))
        holder.setText(R.id.tv_no_order, item.trxId)
        holder.setText(R.id.tv_no_order_bill, "" + item.billNo)
        holder.setText(
            R.id.tv_createTime,
            DateTimeUtil.getLocalDateWithFormat(item.createdAt, "dd/MM/YYYY HH:mm")
        )

    }
}