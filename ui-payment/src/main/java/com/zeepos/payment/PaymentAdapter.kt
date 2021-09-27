package com.zeepos.payment

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.master.PaymentMethod
import com.zeepos.ui_base.views.GlideApp

/**
 * Created by Arif S. on 5/18/20
 */
class PaymentAdapter(data: MutableList<PaymentMethod> = mutableListOf()) :
    BaseQuickAdapter<PaymentMethod, BaseViewHolder>(R.layout.payment_item, data) {

    var selectedPayment: PaymentMethod? = null

    override fun convert(holder: BaseViewHolder, item: PaymentMethod) {
        val paymentName = item.name ?: ""
        holder.setText(R.id.tv_name, paymentName)
        val ivPaymentIcon = holder.getView<ImageView>(R.id.iv_payment)

        val icon = when {
            paymentName.toLowerCase() == ConstVar.PAYMENT_METHOD_GOPAY.toLowerCase() -> R.drawable.ic_gopay
            paymentName.toLowerCase() == ConstVar.PAYMENT_METHOD_OVO.toLowerCase() -> R.drawable.ic_ovo
            paymentName.toLowerCase() == ConstVar.PAYMENT_METHOD_DANA.toLowerCase() -> R.drawable.ic_dana
            paymentName.toLowerCase() == ConstVar.PAYMENT_METHOD_LINKAJA.toLowerCase() -> R.drawable.ic_link_aja
            paymentName.toLowerCase() == ConstVar.PAYMENT_METHOD_SHOPEPAY.toLowerCase() -> R.drawable.logo_shopepay
            else -> R.drawable.logo_merchant
        }

        if (selectedPayment == item) {
            holder.setImageResource(R.id.iv_select, R.drawable.ic_selected)
        } else {
            holder.setImageResource(R.id.iv_select, R.drawable.ic_unselect)
        }

        GlideApp.with(context)
            .load(icon)
            .into(ivPaymentIcon)
    }
}