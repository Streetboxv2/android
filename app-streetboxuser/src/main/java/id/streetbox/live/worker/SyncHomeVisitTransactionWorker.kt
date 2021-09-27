package id.streetbox.live.worker

import android.content.Context
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.zeepos.domain.interactor.syncdata.SyncDataHomeVisitUseCase
import io.reactivex.Single

/**
 * Created by Arif S. on 8/11/20
 */
class SyncHomeVisitTransactionWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val syncDataHomeVisitUseCase: SyncDataHomeVisitUseCase
) : RxWorker(appContext, params) {
    override fun createWork(): Single<Result> {
        val data = inputData.getString("syncData")!!
        return syncDataHomeVisitUseCase.execute(SyncDataHomeVisitUseCase.Params(data))
            .map {
                Result.success()
            }
            .onErrorReturnItem(Result.retry())
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    companion object {
        private const val TAG = "SyncVisitTransactionWorker"

        fun execute(context: Context, uniqueId: String, syncData: String) {
            val constraints = Constraints.Builder()
            constraints.setRequiredNetworkType(NetworkType.CONNECTED)

            val data = Data.Builder()
            data.putString("syncData", syncData)

            val syncWork = OneTimeWorkRequest.Builder(SyncHomeVisitTransactionWorker::class.java)
                .addTag(TAG)
                .setInputData(data.build())
                .setConstraints(constraints.build()).build()
            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(uniqueId, ExistingWorkPolicy.KEEP, syncWork)
        }
    }
}