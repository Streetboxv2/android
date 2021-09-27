package com.streetbox.pos.ui.main.order

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.transaction.ProductSales
import com.zeepos.utilities.NumberUtil

/**
 * Created by Arif S. on 7/2/20
 */
class OrderAdapter(data: MutableList<ProductSales> = mutableListOf()) :

    BaseQuickAdapter<ProductSales, BaseViewHolder>(
        R.layout.item_order, data
    ) {

    init {
        addChildClickViewIds(R.id.rl_remove)
    }

    override fun convert(holder: BaseViewHolder, item: ProductSales) {
        val productPrice = NumberUtil.formatToStringWithoutDecimal(item.price)
        holder.setText(R.id.tv_item_name, "${item.name} x${item.qty}")
        holder.setText(R.id.tv_item_price, productPrice)

    }
}