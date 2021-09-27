package id.streetbox.live.adapter

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import com.zeepos.models.ConstVar
import com.zeepos.models.response.DataItemNotificationBlast
import com.zeepos.utilities.ConvertDateCreateAt
import com.zeepos.utilities.loadImageUrl
import id.streetbox.live.R
import id.streetbox.live.ui.onclick.OnClickItemAny
import kotlinx.android.synthetic.main.layout_item_list_notif.view.*
import kotlinx.android.synthetic.main.layout_item_status_call.view.tvDateStatusCall
import kotlinx.android.synthetic.main.layout_item_status_call.view.tvStatusCall

class AdapterListNotifBlast(
    val dataItemNotificationBlast: DataItemNotificationBlast,
    val onClickItemAny: OnClickItemAny
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.apply {
                tvStatusCall.text = dataItemNotificationBlast.status
                tvDateStatusCall.ConvertDateCreateAt(dataItemNotificationBlast.createdAt.toString())

                tvNameStatusCallNotif.text = dataItemNotificationBlast.name
                imgNotifStatus.loadImageUrl(
                    ConstVar.PATH_IMAGE + dataItemNotificationBlast.logo,
                    itemView.context
                )
            }

            if (dataItemNotificationBlast.status.equals("FINISH", ignoreCase = true)
                || dataItemNotificationBlast.status.equals("REJECT", ignoreCase = true)
                || dataItemNotificationBlast.status.equals("EXPIRE", ignoreCase = true)
            ) {

            } else {
                itemView.setOnClickListener {
                    onClickItemAny.clickItem(dataItemNotificationBlast)
                }
            }

        }
    }

    override fun getLayout(): Int = R.layout.layout_item_list_notif
}