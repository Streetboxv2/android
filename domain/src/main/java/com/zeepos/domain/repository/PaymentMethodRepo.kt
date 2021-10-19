package com.zeepos.domain.repository

import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.QRCodeResponse
import io.reactivex.Single

/**
 * Created by Arif S. on 5/18/20
 */
interface PaymentMethodRepo {
    fun getPaymentMethod(): List<PaymentMethod>
    fun insertUpdate(paymentMethod: PaymentMethod)
    fun getPaymentMethodCloud(merchantId: Long): Single<List<PaymentMethod>>
    fun getQRCodePayment(
        merchantId: Long?,
        amount: Double,
        type: String,
        order: Order,
        orderJson:String
    ): Single<QRCodeResponse>
}