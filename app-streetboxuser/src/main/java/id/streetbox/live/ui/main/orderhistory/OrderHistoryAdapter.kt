package id.streetbox.live.ui.main.orderhistory

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.OrderHistory
import com.zeepos.ui_base.views.GlideApp
import id.streetbox.live.R
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil

/**
 * Created by Arif S. on 7/29/20
 */
class OrderHistoryAdapter(data: MutableList<OrderHistory> = mutableListOf()) :
    BaseQuickAdapter<OrderHistory, BaseViewHolder>(
        R.layout.item_order_history, data
    ), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: OrderHistory) {

        val ivMerchantLogo = holder.getView<ImageView>(R.id.iv_merchant_logo)
        val imageUrl: String =
            ConstVar.PATH_IMAGE + if (item.logo != null) item.logo else ConstVar.EMPTY_STRING

        val dateStr = item.date ?: DateTimeUtil.getDateWithFormat(
            DateTimeUtil.getCurrentDateTime(),
            DateTimeUtil.FORMAT_DATE_WITH_TIME
        )
        if (dateStr.isNotEmpty()) {
            val dateTimeFormatted =
                DateTimeUtil.getDateWithFormat(dateStr, "dd MMM yyyy HH:mm")
            holder.setText(R.id.tv_order_time, dateTimeFormatted)
        }

        holder.setText(R.id.tv_name, item.merchantName)
        holder.setText(R.id.tv_payment_status, item.status)
        holder.setText(R.id.tv_amount, NumberUtil.formatToStringWithoutDecimal(item.amount))
        holder.setText(R.id.tv_trx_id, item.trxId)
        holder.setText(R.id.tv_payment_status_pending, item.status)

        if (item.status == ConstVar.PAYMENT_STATUS_PENDING) {
            holder.setVisible(R.id.tv_payment_status_pending, true)
            holder.setGone(R.id.tv_payment_status, true)
            holder.setVisible(R.id.tv_order_status, true)
        } else {
            holder.setGone(R.id.tv_payment_status_pending, true)
            holder.setVisible(R.id.tv_payment_status, true)
            holder.setGone(R.id.tv_order_status, true)
        }

//        if (item.types == ConstVar.TRANSACTION_TYPE_ORDER) {
//            holder.setGone(R.id.tv_trx_type, true)
//        } else {//home visit type
        holder.setVisible(R.id.tv_trx_type, true)
        holder.setText(R.id.tv_trx_type, item.types)
//        }

        holder.setGone(R.id.tv_order_status, true)


        GlideApp.with(context)
            .load(imageUrl)
            .into(ivMerchantLogo)
    }
}