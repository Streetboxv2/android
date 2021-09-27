package com.streetbox.pos.ui.main.onlineorder

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.master.Product
import com.zeepos.ui_base.views.clickbounce.ClickBounceAnim

class OnlineOrderAdapter(data: MutableList<Product> = mutableListOf()) :
    BaseQuickAdapter<Product, BaseViewHolder>(R.layout.item_order, data) {

    override fun convert(holder: BaseViewHolder, item: Product) {
        holder.setText(R.id.tv_item_name, item.name)

    }
}