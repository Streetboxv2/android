package com.streetbox.pos.ui.setting

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.entities.ListData

/**
 * Created by Arif S. on 7/20/20
 */
class SettingAdapter(data: MutableList<ListData> = mutableListOf()) :
    BaseQuickAdapter<ListData, BaseViewHolder>(
        R.layout.item_simple, data
    ) {

    var selectedPosition = 0

    override fun convert(holder: BaseViewHolder, item: ListData) {
        holder.setText(R.id.tv_title, item.title)
        holder.setImageResource(R.id.iv_icon, item.icon)

        if (selectedPosition == holder.adapterPosition) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.gray_btn_bg_color
                )
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        }
    }
}