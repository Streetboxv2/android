package com.zeepos.streetbox.adapter

import com.chad.library.adapter.base.listener.OnClickItemAny
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import com.zeepos.models.ConstVar
import com.zeepos.models.response.DataItemGetStatusCallFoodTruck
import com.zeepos.streetbox.R
import com.zeepos.utilities.ConvertDateCreateAt
import com.zeepos.utilities.hideView
import com.zeepos.utilities.loadImageUrl
import com.zeepos.utilities.showView
import kotlinx.android.synthetic.main.layout_item_statuscall_foodtruck.view.*

class AdapterStatusCallFoodTruck(
    val dataItemGetStatusCallFoodTruck: DataItemGetStatusCallFoodTruck,
    val onClickItemAny: OnClickItemAny
) :
    Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.apply {
                tvNameStatusCall.text = dataItemGetStatusCallFoodTruck.name
                tvDateStatusCall.ConvertDateCreateAt(dataItemGetStatusCallFoodTruck.createdAt.toString())
                tvQueueFoodTruck.text = (position + 1).toString() // dataItemGetStatusCallFoodTruck.queueNo.toString()

                imgStatusCallFoodTruck.loadImageUrl(
                    ConstVar.PATH_IMAGE + dataItemGetStatusCallFoodTruck.profile_picture,
                    itemView.context
                )

                tvStatusCallCustomer.text = dataItemGetStatusCallFoodTruck.status
                tvPhoneStatusCallCustomer.text = "Phone : " + dataItemGetStatusCallFoodTruck.phone

                if (dataItemGetStatusCallFoodTruck.status.equals("REQUEST")) {
                    hideView(llTvQueue)
                } else {
                    showView(llTvQueue)
                }
            }

            itemView.setOnClickListener {
                onClickItemAny.clickItem(dataItemGetStatusCallFoodTruck, position)
            }
        }
    }

    override fun getLayout(): Int = R.layout.layout_item_statuscall_foodtruck
}