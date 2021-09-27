package com.zeepos.streetbox.ui.operator.main

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.master.User
import com.zeepos.streetbox.R

class OperatorFTAdapter(data: MutableList<User>) :
    BaseQuickAdapter<User, BaseViewHolder>(R.layout.operator_item, data), LoadMoreModule {

    init {
        addChildClickViewIds(R.id.tv_assign)
    }

    override fun convert(holder: BaseViewHolder, item: User) {
        val tvAssign = holder.getView<TextView>(R.id.tv_assign)

        holder.setText(R.id.tv_name, item.name)
    }
}