package com.streetbox.pos.ui.main.product

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.ConstVar
import com.zeepos.models.master.Product
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.ui_base.views.clickbounce.ClickBounceAnim

/**
 * Created by Arif S. on 2019-11-02
 */
class ProductAdapter(data: MutableList<Product> = mutableListOf()) :
    BaseQuickAdapter<Product, BaseViewHolder>(R.layout.item_product, data) {

    override fun convert(holder: BaseViewHolder, item: Product) {
        val ivProduct = holder.getView<ImageView>(R.id.iv_item_image)
        holder.setText(R.id.tv_item_name, item.name)

        val imageUrls = item.photo ?: ConstVar.EMPTY_STRING
        val imageUrl: String =
            ConstVar.PATH_IMAGE + if (imageUrls.isNotEmpty()) item.photo else ConstVar.EMPTY_STRING

        if (item.photo != null && item.photo!!.isNotEmpty()) {
            GlideApp.with(context)
                .load(imageUrl)
                .apply(RequestOptions().placeholder(R.drawable.nomenu))
                .into(ivProduct)
        }

        ClickBounceAnim.setAnimView(holder.itemView)
            .setScale(ClickBounceAnim.Mode.MODE_STATIC_DP, 6f)
    }
}