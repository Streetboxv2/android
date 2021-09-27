package com.zeepos.streetbox.ui.parkingdetail.provider

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.parkingdetail.ParkingDetailAdapter
import com.zeepos.streetbox.ui.parkingdetail.entity.FirstNode

/**
 * Created by Arif S. on 5/14/20
 */
class FirstProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 1
    override val layoutId: Int
        get() = R.layout.node_first_item

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val node = item as FirstNode
        helper.setText(R.id.tv_title, node.getTitle())
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode, payloads: List<Any>) {
        for (payload in payloads) if (payload is Int && payload == ParkingDetailAdapter.EXPAND_COLLAPSE_PAYLOAD) {
            setArrowSpin(helper, item, true)
        }
    }

    private fun setArrowSpin(
        helper: BaseViewHolder,
        data: BaseNode,
        isAnimate: Boolean
    ) {
        val entity = data as FirstNode
        val imageView = helper.getView<ImageView>(R.id.iv_arrow)
        if (entity.isExpanded) {
            if (isAnimate) {
                ViewCompat.animate(imageView).setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .rotation(0f)
                    .start()
            } else {
                imageView.rotation = 0f
            }
        } else {
            if (isAnimate) {
                ViewCompat.animate(imageView).setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .rotation(-90f)
                    .start()
            } else {
                imageView.rotation = -90f
            }
        }
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        val entity: FirstNode = data as FirstNode
        if (entity.isExpanded) {
            getAdapter()?.collapse(
                position,
                parentPayload = ParkingDetailAdapter.EXPAND_COLLAPSE_PAYLOAD
            )
        } else {
            getAdapter()?.expandAndCollapseOther(position, expandPayload = ParkingDetailAdapter.EXPAND_COLLAPSE_PAYLOAD)
        }

    }
}