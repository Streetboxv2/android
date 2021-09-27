package com.zeepos.domain.repository

import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.PaymentSales
import io.reactivex.Single

interface PaymentSalesRepo {
    fun getPaymentSales(): List<PaymentSales>
    fun insertUpdate(paymentSales: PaymentSales)
    fun generatePaymentSales(
        paymentMethod: PaymentMethod,
        status: String,
        order: Order
    ): Single<PaymentSales>
}