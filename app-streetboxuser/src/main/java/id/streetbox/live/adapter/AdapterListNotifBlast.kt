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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AdapterListNotifBlast(
    val dataItemNotificationBlast: DataItemNotificationBlast,
    val onClickItemAny: OnClickItemAny
) : Item() {
    fun isExpired(): Boolean {
        if (dataItemNotificationBlast.expireMinutes != null) {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val d1 = input.parse(dataItemNotificationBlast.createdAt)
            val cal: Calendar = Calendar.getInstance()
            cal.setTime(d1)
            cal.add(Calendar.MINUTE, dataItemNotificationBlast.expireMinutes!!)
            val newTime: String = input.format(cal.getTime())
            val d2 = input.parse(newTime)
            val cal2: Calendar = Calendar.getInstance()
            val currentTime: String = input.format(cal2.getTime())
            val d3 = input.parse(currentTime)

            return d3 >= d2
        }

        return false
    }
    fun isDiffDay(): Boolean {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val d1 = input.parse(dataItemNotificationBlast.createdAt)

        val cal2: Calendar = Calendar.getInstance()
        val currentTimeStr: String = input.format(cal2.getTime())
        val currentTime = input.parse(currentTimeStr)
        val diff: Long = currentTime.time - d1.time
        if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 0) {
            return true
        }
        return false
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.apply {
                tvStatusCall.text = dataItemNotificationBlast.status
                if (dataItemNotificationBlast.status == "ACCEPT") {
                    tvStatusCall.text = "ON THE WAY"
                }
                if ((dataItemNotificationBlast.status == "ONGOING" || dataItemNotificationBlast.status == "ACCEPT") && isExpired()) {
                    tvStatusCall.text = "EXPIRE"
                }

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
                || isExpired()
                || isDiffDay()
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