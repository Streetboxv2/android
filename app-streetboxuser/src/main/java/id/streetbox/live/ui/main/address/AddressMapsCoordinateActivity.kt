package id.streetbox.live.ui.main.address

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.zeepos.utilities.TrackGps
import id.streetbox.live.R
import kotlinx.android.synthetic.main.activity_address_maps_coordinate.*

class AddressMapsCoordinateActivity : AppCompatActivity(), OnMapReadyCallback {
    var supportMapFragment: SupportMapFragment? = null
    var googleMap: GoogleMap? = null
    var trackGps: TrackGps? = null
    var latitude = 0.0
    var longitude = 0.0
    var marker: Marker? = null
    var latCoordinate = 0.0
    var lngCoordinate = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_maps_coordinate)

        initial()
    }

    private fun initial() {
        initCurrentLocation()
        initMaps()

        btnSetCoordinate.setOnClickListener {
            val intent = Intent()
                .putExtra("lat", latCoordinate)
                .putExtra("lng", lngCoordinate)
            setResult(20, intent)
            finish()
        }
    }

    private fun initCurrentLocation() {
        trackGps = TrackGps(this)
        if (trackGps!!.canGetLocation()) {
            latitude = trackGps!!.latitude
            longitude = trackGps!!.longitude
        }
    }

    private fun initMaps() {
        supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        val latLng = LatLng(latitude, longitude)
        latCoordinate = latitude
        lngCoordinate = longitude

        marker = googleMap!!.addMarker(
            MarkerOptions().flat(true).position(latLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.marker_baru)
            ).draggable(true).title("Lokasi Anda")
        )

        marker!!.position = latLng

        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(14f))

        googleMap!!.setOnMapClickListener {
            marker!!.position = it
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLng(it))
            animateMarker(marker!!, it, false)
        }

        googleMap!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragEnd(p0: Marker?) {
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLng(p0?.position))
                val latlngUpdate = p0!!.position
                animateMarker(marker!!, latlngUpdate, false)
            }

            override fun onMarkerDragStart(p0: Marker?) {
            }

            override fun onMarkerDrag(p0: Marker?) {
            }

        })
    }

    fun animateMarker(
        marker: Marker, toPosition: LatLng,
        hideMarker: Boolean
    ) {
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val proj: Projection = googleMap!!.projection
        val startPoint = proj.toScreenLocation(marker.position)
        val startLatLng = proj.fromScreenLocation(startPoint)
        val duration: Long = 500
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation(
                    elapsed.toFloat()
                            / duration
                )
                lngCoordinate = t * toPosition.longitude + (1 - t) * startLatLng.longitude
                latCoordinate = t * toPosition.latitude + (1 - t) * startLatLng.latitude
                marker.position = LatLng(latCoordinate, lngCoordinate)
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                } else {
                    marker.isVisible = !hideMarker
//
                }
            }
        })
    }

}