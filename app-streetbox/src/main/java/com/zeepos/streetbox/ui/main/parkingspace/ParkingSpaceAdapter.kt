package com.zeepos.streetbox.ui.main.parkingspace

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.master.ParkingSpace
import com.zeepos.streetbox.R
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.SharedPreferenceUtil

/**
 * Created by Arif S. on 5/11/20
 */
class ParkingSpaceAdapter(data: MutableList<ParkingSpace> = ArrayList()) :
    BaseQuickAdapter<ParkingSpace, BaseViewHolder>(R.layout.parkingspace_item, data),
    LoadMoreModule {

    private val token: String by lazy {
        SharedPreferenceUtil.getString(context, ConstVar.TOKEN, ConstVar.EMPTY_STRING)
            ?: ConstVar.EMPTY_STRING
    }

    override fun convert(holder: BaseViewHolder, item: ParkingSpace) {
        val ivImage = holder.getView<ImageView>(R.id.iv_parking)
        val imageUrls = item.imagesMeta ?: ArrayList()
        val imageUrl: String =
            ConstVar.PATH_IMAGE + if (imageUrls.isNotEmpty()) item.imagesMeta!![0] else ConstVar.EMPTY_STRING

        holder.setText(R.id.tv_title, item.name)
        holder.setText(R.id.tv_description, item.description)
        holder.setText(R.id.tv_rating, item.rating.toString())

        if (imageUrl.isNotEmpty()) {

            val glideUrl = GlideUrl(
                imageUrl, LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            )
            GlideApp.with(context)
                .load(glideUrl)
                .into(ivImage)
        }
    }

}