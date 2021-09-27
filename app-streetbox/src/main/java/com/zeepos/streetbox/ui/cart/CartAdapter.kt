package com.zeepos.streetbox.ui.cart

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.streetbox.R

/**
 * Created by Arif S. on 5/17/20
 */
class CartAdapter(data: MutableList<ParkingSales>) :
    BaseQuickAdapter<ParkingSales, BaseViewHolder>(
        R.layout.cart_item, data
    ) {
    override fun convert(holder: BaseViewHolder, item: ParkingSales) {
    }
}