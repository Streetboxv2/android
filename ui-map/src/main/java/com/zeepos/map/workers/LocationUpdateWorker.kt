package com.zeepos.map.workers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.*
import com.google.android.gms.location.LocationServices
import com.zeepos.domain.interactor.map.UpdateCurrentLocationToCloud
import com.zeepos.models.ConstVar
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by Arif S. on 6/30/20
 */
class LocationUpdateWorker @Inject internal constructor(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {

//        val taskId = inputData.getLong("taskId", 0)

        try {
            Thread.sleep(5000) //5 seconds cycle
            setUpLocationListener(taskId)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener(taskId: Long) {
        LocationServices.getFusedLocationProviderClient(applicationContext)
            ?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    updateCurrentLocationToCloud(taskId, location.latitude, location.longitude)
                }
            }

        enqueue(
            applicationContext,
            ExistingWorkPolicy.REPLACE,
            taskId,
            updateCurrentLocationToCloud
        )//call itself
    }

    private fun updateCurrentLocationToCloud(taskId: Long, latitude: Double, longitude: Double) {
        updateCurrentLocationToCloud?.let { useCase ->
            val disposable = useCase.execute(
                UpdateCurrentLocationToCloud.Params(
                    taskId,
                    latitude,
                    longitude,
                    0
                )
            )
                .subscribe({
                    Log.d(ConstVar.TAG, "Update success")
                }, {
                    it.printStackTrace()
                })

            compositeDisposable.add(disposable)
        }
    }

    companion object {
        private const val LOCATION = "LOCATION"
        private var updateCurrentLocationToCloud: UpdateCurrentLocationToCloud? = null
        private var compositeDisposable = CompositeDisposable()
        private var taskId: Long = 0

        fun enqueue(
            context: Context,
            existingWorkPolicy: ExistingWorkPolicy,
            taskId: Long,
            updateCurrentLocationToCloud: UpdateCurrentLocationToCloud?
        ) {
            val data = Data.Builder()
            data.putLong("taskId", taskId)

            this.taskId = taskId

            this.updateCurrentLocationToCloud = updateCurrentLocationToCloud
            val updateCurrentLocation = OneTimeWorkRequestBuilder<LocationUpdateWorker>()
                .addTag(LOCATION)
                .setInputData(data.build())
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                LOCATION,
                existingWorkPolicy,
                updateCurrentLocation
            )
        }

        fun stop(context: Context) {
            compositeDisposable.dispose()
            WorkManager.getInstance(context).cancelAllWorkByTag(LOCATION)
        }
    }
}