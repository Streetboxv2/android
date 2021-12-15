package com.streetbox.pos.ui.receipts

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.streetbox.pos.R
import com.zeepos.models.ConstVar
import com.zeepos.models.transaction.Order
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil
import java.util.*

/**
 * Created by Arif S. on 10/7/20
 */
class ReceiptAdapter(data: MutableList<Order> = mutableListOf()) :
    BaseQuickAdapter<Order, BaseViewHolder>(R.layout.item_receipt, data) {
    override fun convert(holder: BaseViewHolder, item: Order) {
        val tanggal = DateTimeUtil.getDateWithFormat(item.businessDate, "dd/MM/YYYY")
       var jam:String = ConstVar.EMPTY_STRING
        if(item.typeOrder.equals("Online")){
            jam = DateTimeUtil.getLocalDateWithFormat(item.dateCreated,"HH:mm")
        }else{
            jam = DateTimeUtil.getLocalDateWithFormat(item.createdAt, "HH:mm")
        }


        val jumlah = NumberUtil.formatToStringWithoutDecimal(item.grandTotal)

//        if (item?.trx.size > 0) {
//            holder.setText(R.id.tv_bill_no, item?.trx[0]?.status)
//        }else {

            holder.setText(R.id.tv_bill_no, item?.orderNo)


            holder.setText(R.id.tv_type_order, item?.typeOrder)
//        }
        holder.setText(R.id.tv_tanggal, tanggal)
        if (item.trxId.isEmpty()) {
            holder.setText(R.id.tv_jam, "-")
        }else {
            holder.setText(R.id.tv_jam, jam)
        }

        if (item.trxId.isEmpty()) {
            holder.setText(R.id.tv_no_trx, "Processing Sync Server...")
        } else {
            holder.setText(R.id.tv_no_trx, item.trxId)
        }
        holder.setText(R.id.tv_jumlah, jumlah)
        if (item.trxId.isEmpty()) {
            holder.setText(R.id.tv_payment_method, "-")
        }else {
            holder.setText(R.id.tv_payment_method, item.typePayment)
        }

        holder.setText(R.id.tv_status_payment, item.status)
    }
}