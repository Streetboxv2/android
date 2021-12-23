package id.streetbox.live.adapter

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import com.zeepos.models.response.DataItemGetStatusCall
import com.zeepos.utilities.ConvertDateCreateAt
import id.streetbox.live.R
import kotlinx.android.synthetic.main.layout_item_status_call.view.*

class AdapterStatusCallEndUser(
    val dataItemGetStatusCall: DataItemGetStatusCall,
    val onClickItemAny: id.streetbox.live.ui.onclick.OnClickItemAny
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.apply {
            itemView.apply {
                tvDateStatusCall.ConvertDateCreateAt(dataItemGetStatusCall.createdAt.toString())
                tvQueueStatusCall.text = dataItemGetStatusCall.queueNo.toString()
                tvNameStatusCall.text = dataItemGetStatusCall.name
                if(dataItemGetStatusCall.status!!.equals("ACCEPT")){
                    tvStatusCall.text = "ON THE WAY"
                }else if(dataItemGetStatusCall.status!!.equals("REJECT") || dataItemGetStatusCall.status!!.equals("REJECTED")){
                        tvStatusCall.text = "REJECT"
                    }
                else{
                    tvStatusCall.text = dataItemGetStatusCall.status
                }

            }
            itemView.setOnClickListener {
                onClickItemAny.clickItem(dataItemGetStatusCall)
            }
        }
    }

    override fun getLayout(): Int = R.layout.layout_item_status_call
}