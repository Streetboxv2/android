package com.zeepos.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zeepos.ui_base.R

object PermissionUtils {

    const val RC_READ_WRITE_PERMISSION = 1

    fun requestAccessFineLocationPermission(activity: AppCompatActivity, requestId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                requestId
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                requestId
            )
        }
    }

    fun isAccessFineLocationGranted(context: Context): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val permission2 = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permission3 = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            permission1 == PackageManager.PERMISSION_GRANTED &&
                    permission2 == PackageManager.PERMISSION_GRANTED && permission3 == PackageManager.PERMISSION_GRANTED
        } else {
            permission1 == PackageManager.PERMISSION_GRANTED &&
                    permission2 == PackageManager.PERMISSION_GRANTED
        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.enable_gps))
            .setMessage(context.getString(R.string.required_for_this_app))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }

    fun isReadWriteStoragePermissionGranted(activity: Activity) {
        val permission1 =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permission2 =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= 23) {
            val isGranted =
                permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    RC_READ_WRITE_PERMISSION
                )
            }
        }
    }

}