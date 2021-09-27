package com.streetbox.pos.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.zeepos.domain.interactor.syncdata.SyncDataEndUserUseCase
import io.reactivex.Single
import java.util.concurrent.TimeUnit

/**
 * Created by Arif S. on 7/8/20
 */
class SyncTransactionScheduledWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val syncDataEndUserUseCase: SyncDataEndUserUseCase
) : RxWorker(appContext, params) {

    override fun createWork(): Single<Result> {
        return syncDataEndUserUseCase.execute(SyncDataEndUserUseCase.Params(""))
            .toSingleDefault(Result.success())
            .doOnSuccess {
                Log.d(TAG, "Sync transaction running...")
            }
            .onErrorReturnItem(Result.retry())
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    companion object {
        private const val TAG = "SyncTransactionWorker"

        fun scheduleSyncTransactionData(context: Context) {
            val constraints = Constraints.Builder()
            constraints.setRequiredNetworkType(NetworkType.CONNECTED)

            val scheduledSyncWork =
                PeriodicWorkRequestBuilder<SyncTransactionScheduledWorker>(15, TimeUnit.MINUTES)
                    .addTag(TAG)
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS
                    )
                    .setConstraints(constraints.build()).build()
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, scheduledSyncWork)
        }
    }

}