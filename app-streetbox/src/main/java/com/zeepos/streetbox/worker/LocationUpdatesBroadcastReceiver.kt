package com.zeepos.streetbox.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult
import com.zeepos.models.entities.FoodTruckLocation
import com.zeepos.models.master.User
import dagger.android.AndroidInjection
import io.objectbox.Box
import io.objectbox.BoxStore
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * Created by Arif S. on 8/8/20
 */
class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var boxStore: BoxStore
    private val box: Box<FoodTruckLocation> by lazy {
        boxStore.boxFor(
            FoodTruckLocation::class.java
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        val taskId = intent.getLongExtra("taskId", 0)
        if (intent.action == ACTION_LOCATION_UPDATES) {
            LocationResult.extractResult(intent)?.let { locationResult ->
                val locations = locationResult.locations.map { location ->
                    val msg = FoodTruckLocation()
                    msg.taskId = taskId
                    msg.latitude = location.latitude
                    msg.longitude = location.longitude
                    msg.tag = "scheduler 2"

//                    EventBus.getDefault().post(msg)
                    box.removeAll()
                    box.put(msg)
                }
            }
        }
    }

    companion object {
        const val ACTION_LOCATION_UPDATES =
            "com.zeepos.streetbox.LocationUpdatesBroadcastReceiver.ACTION_LOCATION_UPDATES"
        const val EXTRA_LOCATION = "location"
    }
}