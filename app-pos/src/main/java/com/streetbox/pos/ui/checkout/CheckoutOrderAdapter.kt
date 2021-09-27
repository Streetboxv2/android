package com.streetbox.pos.ui.checkout

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.transaction.ProductSales
import com.zeepos.utilities.NumberUtil

/**
 * Created by Arif S. on 7/12/20
 */
class CheckoutOrderAdapter(data: MutableList<ProductSales> = mutableListOf()) :
    BaseQuickAdapter<ProductSales, BaseViewHolder>(
        R.layout.item_order_checkout, data
    ) {
    override fun convert(holder: BaseViewHolder, item: ProductSales) {
        val productPrice = NumberUtil.formatToStringWithoutDecimal(item.price)
        holder.setText(R.id.tv_item_name, "${item.name} x${item.qty}")
        holder.setText(R.id.tv_item_price, productPrice)
    }
}