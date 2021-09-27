package id.streetbox.live.uiAPIs

import android.app.Activity
import com.zeepos.payment.PaymentUiEvent
import id.streetbox.live.worker.SyncHomeVisitTransactionWorker
import id.streetbox.live.worker.SyncTransactionWorker
import javax.inject.Inject

/**
 * Created by Arif S. on 7/29/20
 */
class PaymentUiEventImpl @Inject internal constructor() : PaymentUiEvent {

    override fun onPaymentFinish(activity: Activity, orderUniqueId: String) {
        SyncTransactionWorker.syncTransactionData(activity, orderUniqueId)
    }

    override fun onHomeVisitPaymentFinish(activity: Activity, uniqueId: String, data: String) {
        SyncHomeVisitTransactionWorker.execute(activity, uniqueId, data)
    }
}