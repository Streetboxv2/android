package com.zeepos.payment

import android.app.Activity

/**
 * Created by Arif S. on 7/29/20
 */
interface PaymentUiEvent {
    fun onPaymentFinish(activity: Activity, orderUniqueId: String)
    fun onHomeVisitPaymentFinish(activity: Activity, uniqueId: String, data: String)
}