package com.zeepos.map.utils

import android.content.Context
import android.graphics.*
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.zeepos.map.R
import kotlin.math.*


object MapUtils {

    private const val TAG = "MapUtils"

    fun init(context: Context) {
        Places.initialize(context, context.getString(R.string.google_maps_key))
    }

    fun getCarBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_car)
        return Bitmap.createScaledBitmap(bitmap, 50, 100, false)
    }

    fun getBitmap(context: Context, imageDrawable: Int): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, imageDrawable)
        return createScaledBitmap(bitmap)
    }

    private fun createScaledBitmap(bitmap: Bitmap): Bitmap {
        var width: Int = bitmap.width
        var height: Int = bitmap.height
        var maxSize = 100

        when {
            width > height -> {
                // landscape
                if (width < maxSize)
                    maxSize = width
                val ratio = width.toFloat() / maxSize
                width = maxSize
                height = (height / ratio).toInt()
            }
            height > width -> {
                // portrait
                if (height < maxSize)
                    maxSize = height
                val ratio = height.toFloat() / maxSize
                height = maxSize
                width = (width / ratio).toInt()
            }
            else -> {
                // square
                if (height < maxSize || width < maxSize)
                    maxSize = height
                height = maxSize
                width = maxSize
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    fun getPickupDestinationBitmap(): Bitmap {
        val height = 20
        val width = 20
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.parseColor("#17A59C")
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
//        canvas.drawCircle(0F, 0F, 40f, paint)
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    fun getDestinationBitmap(): Bitmap {
        val height = 20
        val width = 20
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.parseColor("#FFCE0E")
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    fun getRotation(start: LatLng, end: LatLng): Float {
        val latDifference: Double = abs(start.latitude - end.latitude)
        val lngDifference: Double = abs(start.longitude - end.longitude)
        var rotation = -1F
        when {
            start.latitude < end.latitude && start.longitude < end.longitude -> {
                rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
            }
            start.latitude >= end.latitude && start.longitude < end.longitude -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
            }
            start.latitude >= end.latitude && start.longitude >= end.longitude -> {
                rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
            }
            start.latitude < end.latitude && start.longitude >= end.longitude -> {
                rotation =
                    (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
//        Log.d(TAG, "getRotation: $rotation")
        return rotation
    }

    fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val theta = lng1 - lng2
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(
                deg2rad(theta)
            )

        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return (dist)
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    fun metersToMiles(meters: Double): Double {
        return meters / 1609.3440057765
    }

    fun milesToMeters(miles: Double): Double {
        return 0.0006213711 * miles
    }

    fun getDistance(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Double {
        val startPoint = Location("start")
        startPoint.latitude = startLat
        startPoint.longitude = startLng

        val endPoint = Location("end")
        endPoint.latitude = endLat
        endPoint.longitude = endLng

        return startPoint.distanceTo(endPoint).toDouble()
    }
}