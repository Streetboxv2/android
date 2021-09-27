package com.streetbox.pos.ui.main.onlineorder.orderbill

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.PaymentSales
import com.zeepos.models.transaction.ProductSales
import com.zeepos.utilities.NumberUtil

class OrderBillAdapter(data: MutableList<ProductSales> = mutableListOf()) :
    BaseQuickAdapter<ProductSales, BaseViewHolder>(
        R.layout.item_order_bill, data
    ) {


    override fun convert(holder: BaseViewHolder, item: ProductSales) {
        holder.setText(R.id.tv_item_name, ""+item.qty+" "+item.name)
        if(item.notes.isNotEmpty()) {
            holder.setText(R.id.tv_item_desc, "Notes: " + item.notes)
            holder.setGone(R.id.tv_item_desc,false)
        }else{
            holder.setGone(R.id.tv_item_desc,true)
        }
        holder.setText(R.id.tv_item_price, ""+NumberUtil.formatToStringWithoutDecimal(item.price))

    }
}