package com.zeepos.streetbox.ui.parkingdetail.provider

import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.parkingdetail.entity.SecondNode

/**
 * Created by Arif S. on 5/14/20
 */
class SecondProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 2
    override val layoutId: Int
        get() = R.layout.node_second_item

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val node = item as SecondNode
        helper.setText(R.id.tv_title, node.getTitle())
    }
}