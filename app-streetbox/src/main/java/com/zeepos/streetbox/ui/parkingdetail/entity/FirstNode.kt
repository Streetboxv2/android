package com.zeepos.streetbox.ui.parkingdetail.entity

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode

/**
 * Created by Arif S. on 5/14/20
 */
class FirstNode(
    override val childNode: MutableList<BaseNode>,
    private val title: String
) : BaseExpandNode() {

    fun getTitle(): String {
        return title
    }

    init {
        isExpanded = false
    }
}