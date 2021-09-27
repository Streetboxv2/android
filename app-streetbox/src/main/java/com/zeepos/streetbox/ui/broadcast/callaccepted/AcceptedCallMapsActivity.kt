package com.zeepos.streetbox.ui.broadcast.callaccepted

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.zeepos.models.ConstVar
import com.zeepos.models.response.DataItemGetStatusCallFoodTruck
import com.zeepos.models.response.ResponseDirectionsMaps
import com.zeepos.networkmaps.ApiClientMaps
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.broadcast.BroadCastViewEvent
import com.zeepos.streetbox.ui.broadcast.BroadCastViewModel
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.*
import kotlinx.android.synthetic.main.activity_call_accepted.*
import kotlinx.android.synthetic.main.activity_customer_call_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcceptedCallMapsActivity : BaseActivity<BroadCastViewEvent, BroadCastViewModel>(),
    OnMapReadyCallback {
    var supportMapFragment: SupportMapFragment? = null
    var googleMap: GoogleMap? = null
    var trackGps: TrackGps? = null
    var latitude = 0.0
    var longitude = 0.0
    var marker: Marker? = null
    var latitudeEnd = 0.0
    var longitudeEnd = 0.0
    var dataItemGetStatusCallFoodTruck: DataItemGetStatusCallFoodTruck? = null

    override fun initResourceLayout(): Int {
        return R.layout.activity_call_accepted
    }

    override fun init() {
        dataItemGetStatusCallFoodTruck =
            intent.getParcelableExtra(ConstVar.DATA_ITEM_STATUS_CALL_FOODTRUCK)
        viewModel = ViewModelProvider(this, viewModeFactory).get(BroadCastViewModel::class.java)
        initial()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initMaps()
        initData()
        initOnclick()
    }

    private fun initOnclick() {
        btnFinishOrder.setOnClickListener {
            showLoading()
            viewModel.callFinishOrder(dataItemGetStatusCallFoodTruck?.id.toString())
        }

        btnOnProses.setOnClickListener {
            showLoading()
            viewModel.callOnprocessOrder(dataItemGetStatusCallFoodTruck?.id.toString(), "onprocess")
        }
    }

    private fun initData() {
        latitudeEnd = dataItemGetStatusCallFoodTruck?.latitudeEndUser!!
        longitudeEnd = dataItemGetStatusCallFoodTruck?.longitudeEndUser!!

        tvNameStatusCallDetail.text = dataItemGetStatusCallFoodTruck?.name
        tvDateStatusCallDetail.ConvertDateCreateAt(dataItemGetStatusCallFoodTruck?.createdAt.toString())
        tvQueueFoodTruckDetail.text = dataItemGetStatusCallFoodTruck?.queueNo.toString()
        imgStatusCallFoodTruckDetail.loadImageUrl(
            ConstVar.PATH_IMAGE + dataItemGetStatusCallFoodTruck?.profile_picture,
            this
        )

        if (dataItemGetStatusCallFoodTruck!!.status.equals("EXPIRE") ||
            dataItemGetStatusCallFoodTruck!!.status.equals("FINISH")
        ) {
            hideView(llFinishOrder)
        } else if (dataItemGetStatusCallFoodTruck!!.status.equals("ONPROCESS")
        ) {
            hideView(btnOnProses)
            showView(llFinishOrder)
        } else showView(llFinishOrder)

        if (dataItemGetStatusCallFoodTruck!!.status.equals("REQUEST")) {
            hideView(llQueueAccept)
        } else showView(llQueueAccept)

    }

    override fun onEvent(useCase: BroadCastViewEvent) {
        when (useCase) {
            is BroadCastViewEvent.OnSuccessListFinishOrder -> {
                dismissLoading()
                showToastExt("Finish Order", this)
                finish()
            }
            is BroadCastViewEvent.OnSuccessStatusOrder -> {
                dismissLoading()
                showToastExt("Process Order", this)
                finish()
            }
            is BroadCastViewEvent.OnFailed -> {
                dismissLoading()
            }
        }
    }

    private fun initial() {
        initCurrentLocation()
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

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        val latLng = LatLng(
            dataItemGetStatusCallFoodTruck?.latitudeFoodtruck!!,
            dataItemGetStatusCallFoodTruck?.longitudeFoodtruck!!
        )
        marker = googleMap!!.addMarker(
            MarkerOptions().flat(true).position(latLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_foodtruck)
            ).title("Lokasi Anda")
        )

        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(14f))

        val origin =
            "${dataItemGetStatusCallFoodTruck?.latitudeFoodtruck},${dataItemGetStatusCallFoodTruck?.longitudeFoodtruck}"
        val destination =
            "${dataItemGetStatusCallFoodTruck?.latitudeEndUser},${dataItemGetStatusCallFoodTruck?.longitudeEndUser}"

        callApiDirections(origin, destination)
    }

    private fun callApiDirections(origin: String, destination: String) {
        showLoading()
        ApiClientMaps
            .ApiClient()
            ?.requestDirectionMaps(
                origin,
                destination,
                application.getString(R.string.google_maps_key)
            )
            ?.enqueue(object : Callback<ResponseDirectionsMaps?> {
                override fun onResponse(
                    call: Call<ResponseDirectionsMaps?>,
                    response: Response<ResponseDirectionsMaps?>
                ) {
                    val responseDirectionsMaps = response.body()
                    val list =
                        MapUtils.decodePoly(responseDirectionsMaps?.routes?.get(0)?.overviewPolyline?.points)

                    val destinationLatlng = LatLng(latitudeEnd, longitudeEnd)
                    addPolyline(list, destinationLatlng)
                }

                override fun onFailure(call: Call<ResponseDirectionsMaps?>, t: Throwable) {
                }

            })
    }

    fun addPolyline(list: MutableList<LatLng>, destination: LatLng) {
        dismissLoading()
        for (z in 0 until list.size - 1) {
            val src: LatLng = list[z]
            val dest: LatLng = list[z + 1]
            googleMap!!.addMarker(
                MarkerOptions().flat(true).position(destination).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_customer)
                ).title("Lokasi Tujuan")
            )

            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.BLUE)
            polylineOptions.width(10f)
            polylineOptions.add(
                LatLng(src.latitude, src.longitude),
                LatLng(dest.latitude, dest.longitude)
            )
            googleMap!!.addPolyline(polylineOptions)
        }
    }

}