package com.streetbox.pos.ui.receipts

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.transaction.ProductSales
import com.zeepos.utilities.NumberUtil

/**
 * Created by Arif S. on 10/13/20
 */
class ReceiptDetailAdapter(data: MutableList<ProductSales> = mutableListOf()) :
    BaseQuickAdapter<ProductSales, BaseViewHolder>(
        R.layout.item_receipt_detail, data
    ) {
    override fun convert(holder: BaseViewHolder, item: ProductSales) {
        val price = NumberUtil.formatToStringWithoutDecimal(item.price)
        val subtotal = NumberUtil.formatToStringWithoutDecimal(item.price * item.qty)
        holder.setText(R.id.tv_name, item.name)
        holder.setText(R.id.tv_subtotal, subtotal)
        holder.setText(R.id.tv_qty, "${item.qty} x $price")
    }
}