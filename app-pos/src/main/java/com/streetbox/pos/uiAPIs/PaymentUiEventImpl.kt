package com.streetbox.pos.uiAPIs

import android.app.Activity
import com.zeepos.payment.PaymentUiEvent
import javax.inject.Inject

/**
 * Created by Arif S. on 7/29/20
 */
class PaymentUiEventImpl @Inject internal constructor() : PaymentUiEvent {
    override fun onPaymentFinish(activity: Activity, orderUniqueId: String) {

    }

    override fun onHomeVisitPaymentFinish(activity: Activity, uniqueId: String, data: String) {

    }
}