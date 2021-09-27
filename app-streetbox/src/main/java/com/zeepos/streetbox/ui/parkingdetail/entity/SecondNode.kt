package com.zeepos.streetbox.ui.parkingdetail.entity

import com.chad.library.adapter.base.entity.node.BaseNode

/**
 * Created by Arif S. on 5/14/20
 */
class SecondNode(
    private val title: String
) : BaseNode() {
    fun getTitle(): String {
        return title
    }

    override val childNode: MutableList<BaseNode>?
        get() = null
}