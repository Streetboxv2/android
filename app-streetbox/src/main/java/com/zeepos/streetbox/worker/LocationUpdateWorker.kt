package com.zeepos.streetbox.worker

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.work.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.zeepos.domain.interactor.map.UpdateCurrentLocationToCloud
import com.zeepos.models.entities.FoodTruckLocation
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


/**
 * Created by Arif S. on 7/8/20
 */
class LocationUpdateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private var updateCurrentLocationToCloud: UpdateCurrentLocationToCloud,
    boxStore: BoxStore
) : RxWorker(appContext, params) {

    private val box: Box<FoodTruckLocation> by lazy {
        boxStore.boxFor(
            FoodTruckLocation::class.java
        )
    }

//    private var fusedLocationProviderClient: FusedLocationProviderClient = FusedLocationProviderClient(appContext)

    override fun createWork(): Single<Result> {
        val taskId = inputData.getLong("taskId", 0)
        Log.d(TAG, "update task id location -> $taskId")

        return getCurrentLocation()
            .observeOn(Schedulers.io())
            .delay(5, TimeUnit.SECONDS)
            .flatMap {
                Log.d(TAG, "flatMap Thread -> ${Thread.currentThread().name}")

                val dataLocation = box.all
                val foodTruckLocation = if (dataLocation.isNotEmpty()) dataLocation[0] else null

                if (foodTruckLocation != null) {
                    return@flatMap updateCurrentLocationToCloud.execute(
                        UpdateCurrentLocationToCloud.Params(
                            taskId,
                            foodTruckLocation.latitude,
                            foodTruckLocation.longitude,
                            0
                        )
                    )
                        .toSingleDefault(Result.success())
                }

                Single.fromCallable { Result.success() }

            }
            .delay(10, TimeUnit.SECONDS)
            .doOnSuccess {
                Log.d(TAG, "doOnSuccess Thread -> ${Thread.currentThread().name}")
                try {
                    enqueue(appContext, ExistingWorkPolicy.APPEND, taskId)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            .doOnError {
                Log.d(TAG, "update location Failed -> ${it.message}")
            }
            .onErrorReturnItem(Result.retry())
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Single<Location> {
        return Single.create {
            Log.d(TAG, "Single.create Thread -> ${Thread.currentThread().name}")

//            locationCallback?.let {callback ->
//                fusedLocationProviderClient.removeLocationUpdates(callback)
//            }
//
//            locationCallback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    Log.d(
//                        TAG,
//                        "onLocationResult Thread -> ${Thread.currentThread().name}"
//                    )
//
//                    var mLocation: Location? = null
//
//                    for (loc in locationResult.locations) {
//                        mLocation = loc
//                    }
//
//                    if (mLocation != null) {
//                        val msg = FoodTruckLocation()
//                        msg.taskId = 0
//                        msg.latitude = mLocation.latitude
//                        msg.longitude = mLocation.longitude
//                        msg.tag = "scheduler 2"
//
//                        EventBus.getDefault().post(msg)
//                        it.onSuccess(mLocation)
//                        //remove callback when location found, avoid infinite loops
//                        fusedLocationProviderClient.removeLocationUpdates(
//                            locationCallback
//                        )
//                    }
//                }
//            }

            val locationRequest =
                LocationRequest().setInterval(5000).setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            FusedLocationProviderClient(appContext).requestLocationUpdates(
                locationRequest, locationUpdatePendingIntent
            )

            it.onSuccess(Location(""))
        }
    }

    /**
     * Creates default PendingIntent for location changes.
     *
     * Note: We use a BroadcastReceiver because on API level 26 and above (Oreo+), Android places
     * limits on Services.
     */
    private val locationUpdatePendingIntent: PendingIntent by lazy {
        val taskId = inputData.getLong("taskId", 0)
        val intent = Intent(appContext, LocationUpdatesBroadcastReceiver::class.java)
        intent.action = LocationUpdatesBroadcastReceiver.ACTION_LOCATION_UPDATES
//        intent.putExtra("taskId", taskId)
        PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        private const val TAG = "LocationUpdateWorker"
//        private var fusedLocationProviderClient: FusedLocationProviderClient? = null
//        private var locationCallback: LocationCallback? = null

        fun enqueue(context: Context, existingWorkPolicy: ExistingWorkPolicy, taskId: Long) {
            val constraints = Constraints.Builder()
            constraints.setRequiredNetworkType(NetworkType.CONNECTED)

            val data = Data.Builder()
            data.putLong("taskId", taskId)

            val updateLocationWork = OneTimeWorkRequest.Builder(LocationUpdateWorker::class.java)
                .addTag(TAG)
                .setInputData(data.build())
                .setConstraints(constraints.build()).build()
            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(TAG, existingWorkPolicy, updateLocationWork)
        }

        fun stop(context: Context) {
//            locationCallback?.let {
//                fusedLocationProviderClient?.removeLocationUpdates(it)
//            }

            WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
        }
    }

}