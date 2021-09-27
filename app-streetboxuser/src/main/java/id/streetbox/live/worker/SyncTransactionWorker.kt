package id.streetbox.live.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.zeepos.domain.interactor.syncdata.SyncDataEndUserUseCase
import io.reactivex.Single

/**
 * Created by Arif S. on 7/8/20
 */
class SyncTransactionWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val syncDataEndUserUseCase: SyncDataEndUserUseCase
) : RxWorker(appContext, params) {

    override fun createWork(): Single<Result> {
        val orderUniqueId = inputData.getString("id")!!
        Log.d(TAG, "sync orderUniqueId -> $orderUniqueId")

        return syncDataEndUserUseCase.execute(SyncDataEndUserUseCase.Params(orderUniqueId))
            .toSingleDefault(Result.success())
            .doOnSuccess {
                Log.d(TAG, "Sync transaction success...")
            }
            .doOnError {
                Log.d(TAG, "Sync Failed -> ${it.message}")
            }
            .onErrorReturnItem(Result.retry())
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    companion object {
        private const val TAG = "SyncTransactionWorker"

        fun syncTransactionData(context: Context, orderUniqueId: String) {
            val constraints = Constraints.Builder()
            constraints.setRequiredNetworkType(NetworkType.CONNECTED)

            val data = Data.Builder()
            data.putString("id", orderUniqueId)

            val syncWork = OneTimeWorkRequest.Builder(SyncTransactionWorker::class.java)
                .addTag(TAG)
                .setInputData(data.build())
                .setConstraints(constraints.build()).build()
            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(orderUniqueId, ExistingWorkPolicy.KEEP, syncWork)
        }
    }

}