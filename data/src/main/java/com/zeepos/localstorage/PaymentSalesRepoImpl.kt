package com.zeepos.localstorage

import com.zeepos.domain.repository.PaymentSalesRepo
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.PaymentSales
import com.zeepos.models.transaction.PaymentSales_
import com.zeepos.remotestorage.RemoteService
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Single
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 7/28/20
 */
class PaymentSalesRepoImpl @Inject constructor(
    private val retrofit: Retrofit,
    boxStore: BoxStore
) : PaymentSalesRepo {

    private val box: Box<PaymentSales> = boxStore.boxFor(
        PaymentSales::class.java
    )

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    override fun getPaymentSales(): List<PaymentSales> {
        TODO("Not yet implemented")
    }

    override fun insertUpdate(paymentSales: PaymentSales) {
        box.put(paymentSales)
    }

    override fun generatePaymentSales(
        paymentMethod: PaymentMethod,
        status: String,
        order: Order
    ): Single<PaymentSales> {
        return Single.fromCallable {
            val paymentSales = ObjectFactory.createPaymentSales(paymentMethod, status, order)
            box.put(paymentSales)
            box.query().equal(PaymentSales_.uniqueId, paymentSales.uniqueId).build().findFirst()
        }

    }
}