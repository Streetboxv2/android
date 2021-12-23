package id.streetbox.live.ui.main.doortodoor.notification

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.zeepos.map.utils.AnimationUtils
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.MapData
import com.zeepos.models.response.DataFoodTruck
import com.zeepos.models.response.DataItemGetStatusCall
import com.zeepos.models.response.DataItemNotificationBlast
import com.zeepos.models.response.ResponseDirectionsMaps
import com.zeepos.networkmaps.ApiClientMaps
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.*
import id.streetbox.live.R
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewEvent
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewModel
import id.streetbox.live.utils.showErrorMessageThrowable
import kotlinx.android.synthetic.main.activity_notification_doorto_door_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NotificationDoortoDoorMapsActivity :
    BaseActivity<DoortoDoorViewEvent, DoortoDoorViewModel>() {
    var supportMapFragment: SupportMapFragment? = null
    var googleMap: GoogleMap? = null
    var googleMapFoodTruck: GoogleMap? = null

    var trackGps: TrackGps? = null
    var latitude = 0.0
    var longitude = 0.0
    var marker: Marker? = null
    var latitudeEnd = 0.0
    var longitudeEnd = 0.0
    var dataItemNotificationBlast: DataItemNotificationBlast? = null
    var dataItemGetStatusCall: DataItemGetStatusCall? = null
    var igMerchant: String = ""
    var origin: String? = null
    var destination: String? = null
    var latlngUser: LatLng? = null
    private var greyPolyLineList = arrayListOf<Polyline>()
    private var orangePolyLineList = arrayListOf<Polyline>()
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null

    override fun initResourceLayout(): Int {
        return R.layout.activity_notification_doorto_door_maps
    }

    override fun init() {
        dataItemGetStatusCall = intent.getParcelableExtra("dataStatus")
        dataItemNotificationBlast = intent.getParcelableExtra(ConstVar.DATA_ITEM_NOTIF)
        viewModel = ViewModelProvider(this, viewModeFactory).get(DoortoDoorViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        if (dataItemGetStatusCall != null)
            initGetDataStatusCall()
        else initGetData()

        initMaps()
        initOnClick()
    }

    private fun initGetDataStatusCall() {
        latlngUser = LatLng(
            dataItemGetStatusCall?.latitudeEndUser!!,
            dataItemGetStatusCall?.longitudeEndUser!!
        )
        igMerchant = dataItemGetStatusCall?.ig_account.toString()
        latitudeEnd = dataItemGetStatusCall?.latitudeEndUser!!
        longitudeEnd = dataItemGetStatusCall!!.longitudeEndUser!!


        if(dataItemGetStatusCall?.status!!.equals("ACCEPT")){
            tvStatusCallDetailNotif.text = "On The Way"
        }else if (dataItemGetStatusCall?.status!!.equals("REJECTED") || dataItemGetStatusCall?.status!!.equals("REJECT")){
            tvStatusCallDetailNotif.text = "EXPIRE"
        }else{
            tvStatusCallDetailNotif.text = dataItemGetStatusCall!!.status
        }
        tvNameUserNotifDetail.text = dataItemGetStatusCall?.name

        tvDateStatusCallDetailNotif.ConvertDateCreateAt(dataItemGetStatusCall!!.createdAt.toString())
        tvPlatNomorFoodtruck.text = "Plat Nomor : " + dataItemGetStatusCall!!.platNomor

        imgNotifUserDetail.loadImageUrl(ConstVar.PATH_IMAGE + dataItemGetStatusCall!!.logo, this)

        if (!dataItemGetStatusCall!!.status.equals("ONGOING")) {
            hideView(btnCallFoodTruck)
        } else showView(btnCallFoodTruck)

        if (dataItemGetStatusCall!!.status.equals("ACCEPT")) {
            showView(rlRefreshLocation)
        } else hideView(rlRefreshLocation)

    }

    override fun onResume() {
        super.onResume()
        if (dataItemGetStatusCall != null)
            viewModel.callGetLocFoodTruck(dataItemGetStatusCall?.foodtruckId.toString())
        else viewModel.callGetLocFoodTruck(dataItemNotificationBlast?.foodtruckId.toString())

    }

    fun isExpired(): Boolean {
        if (dataItemNotificationBlast?.expireMinutes != null) {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val d1 = input.parse(dataItemNotificationBlast!!.createdAt)
            val cal: Calendar = Calendar.getInstance()
            cal.setTime(d1)
            cal.add(Calendar.MINUTE, dataItemNotificationBlast!!.expireMinutes!!)
            val newTime: String = input.format(cal.getTime())
            val d2 = input.parse(newTime)
            val cal2: Calendar = Calendar.getInstance()
            val currentTime: String = input.format(cal2.getTime())
            val d3 = input.parse(currentTime)

            return d3 >= d2
        }
        return false
    }

    private fun initOnClick() {
        btnCallFoodTruck.setOnClickListener {
            if (!isExpired()) {
                showLoading()
                viewModel.callReqFoodTruck(dataItemNotificationBlast?.id.toString())
            } else {
                Toast.makeText(this,"EXPIRED", Toast.LENGTH_LONG).show()
            }
        }

        imgBackMaps.setOnClickListener {
            finish()
        }

        imgIgEndUser.setOnClickListener {
            val intent = intentPageData(this, NotifInstagramActivity::class.java)
                .putExtra("merchangIg", igMerchant)
            startActivity(intent)
        }

        rlRefreshLocation.setOnClickListener {
            showLoading()
            if (dataItemGetStatusCall != null)
                viewModel.callGetLocFoodTruck(dataItemGetStatusCall?.foodtruckId.toString())
            else viewModel.callGetLocFoodTruck(dataItemNotificationBlast?.foodtruckId.toString())
        }
    }

    private fun initGetData() {
        latlngUser = LatLng(
            dataItemNotificationBlast?.latitudeEndUser!!,
            dataItemNotificationBlast?.longitudeEndUser!!
        )
        igMerchant = dataItemNotificationBlast?.ig_account.toString()
        latitudeEnd = dataItemNotificationBlast?.latitudeEndUser!!
        longitudeEnd = dataItemNotificationBlast?.longitudeEndUser!!
        tvStatusCallDetailNotif.text = dataItemNotificationBlast?.status
        tvNameUserNotifDetail.text = dataItemNotificationBlast?.name
        tvDateStatusCallDetailNotif.ConvertDateCreateAt(dataItemNotificationBlast?.createdAt.toString())
        tvPlatNomorFoodtruck.text = "Plat Nomor : " + dataItemNotificationBlast!!.platNomor

        imgNotifUserDetail.loadImageUrl(
            ConstVar.PATH_IMAGE + dataItemNotificationBlast!!.logo,
            this
        )

        if (!dataItemNotificationBlast!!.status.equals("ONGOING") || isExpired()) {
            hideView(btnCallFoodTruck)
        } else showView(btnCallFoodTruck)
    }


    override fun onEvent(useCase: DoortoDoorViewEvent) {
        when (useCase) {
            is DoortoDoorViewEvent.OnSuccessCallFoodTruck -> {
                dismissLoading()
                finish()
                showToastExt("Success Calling Food Truck", this)
            }
            is DoortoDoorViewEvent.OnFailed -> {
                val throwable = useCase.throwable
                showToastExt(showErrorMessageThrowable(throwable), this)
                dismissLoading()
            }
            is DoortoDoorViewEvent.OnSuccessGetLocFoodTruck -> {
                dismissLoading()
                val dataItem = useCase.jsonObject.data
                println("respon Dapat latlng ${dataItem?.latitude}")
                supportMapFragment?.getMapAsync {
                    googleMap = it
                    googleMapFoodTruck = it
                    updateMarkerFoodTruck(dataItem)
                }
            }
        }
    }

    private fun updateMarkerFoodTruck(dataItem: DataFoodTruck?) {

        //latlng foodtruck
        val latLng = LatLng(dataItem?.latitude!!, dataItem.longitude!!)
//        val latLngDest = LatLng(dataItemGetStatusCall?.latitudeEndUser!!, dataItemGetStatusCall?.longitudeEndUser!!)
//        marker?.remove()

        showPath(latLng,latlngUser)
//        marker = googleMapFoodTruck!!.addMarker(
//            MarkerOptions().flat(true).position(latLng).icon(
//                BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_foodtruck)
//            ).title(dataItemNotificationBlast?.name)
//        )
//        googleMapFoodTruck!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))
//        marker?.position = latLng
//        animateMarker(marker!!, latLng, false)


        origin = "${dataItem.latitude},${dataItem.longitude}"

//        googleMapFoodTruck!!.addMarker(
//            MarkerOptions().flat(true).position(latlngUser).icon(
//                BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_customer)
//            ).title("Lokasi User")
//        )

        if (dataItemGetStatusCall != null) {
            destination =
                "${dataItemGetStatusCall?.latitudeEndUser},${dataItemGetStatusCall?.longitudeEndUser}"
        } else {
            destination =
                "${dataItemNotificationBlast?.latitudeEndUser},${dataItemNotificationBlast?.longitudeEndUser}"
        }

        callApiDirections(origin!!, destination!!)


//        //latlng primary address user
//        val latLngEnd = LatLng(latitudeEnd, longitudeEnd)
//        googleMap!!.addMarker(
//            MarkerOptions().flat(true).position(latLngEnd).icon(
//                BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_customer)
//            ).title("Lokasi Anda")
//        )
    }

    private fun moveCamera(latLng: LatLng?) {
        googleMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 19.0f
            )
        )
//        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun showPath(latLngData: LatLng?,latLngData1: LatLng?) {
        val latLngList = arrayListOf<LatLng>()
//        val builder = LatLngBounds.Builder()
//        for (latLngData in latLngList.latLngList) {
            val latLng = LatLng(latLngData!!.latitude, latLngData.longitude)
          val latLngDest = LatLng(latLngData1!!.latitude, latLngData1.longitude)
//            builder.include(latLng)
            latLngList.add(latLng)
        latLngList.add(latLngDest)
//        }
//        val bounds = builder.build()

        moveCamera(latLngList[0])
//        googleMapFoodTruck?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))
//        val polylineOptions = PolylineOptions()
//        polylineOptions.color(Color.GRAY)
//        polylineOptions.width(5f)
//        polylineOptions.addAll(latLngList)
//        val greyPolyLine = googleMapFoodTruck!!.addPolyline(polylineOptions)
//        greyPolyLineList.add(greyPolyLine)

//        val blackPolylineOptions = PolylineOptions()
//        blackPolylineOptions.width(5f)
//        blackPolylineOptions.color(Color.parseColor("#EF6623"))
//        val orangePolyLine = googleMapFoodTruck!!.addPolyline(blackPolylineOptions)
//        orangePolyLineList.add(orangePolyLine)

        originMarker = addOriginPickupMarkerAndGet(latLngList[0])
        originMarker!!.setAnchor(0.5f, 0.5f)

        destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
        destinationMarker?.setAnchor(0.5f, 0.5f)

//        val polylineAnimator = AnimationUtils.polyLineAnimator()
//        polylineAnimator.addUpdateListener { valueAnimator ->
//            val percentValue = (valueAnimator.animatedValue as Int)
//            val index = (greyPolyLine.points!!.size * (percentValue / 100.0f)).toInt()
//            orangePolyLine.points = greyPolyLine.points!!.subList(0, index)
//        }
//        polylineAnimator.start()
    }

    private fun addOriginPickupMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(com.zeepos.map.utils.MapUtils.getPickupDestinationBitmap())
        return googleMap!!.addMarker(
            MarkerOptions().flat(true).position(latLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_foodtruck)
            ).title(dataItemNotificationBlast?.name))

    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(com.zeepos.map.utils.MapUtils.getDestinationBitmap())
        return googleMap!!.addMarker(
            MarkerOptions().flat(true).position(latlngUser).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_customer)
            ).title("Lokasi User")
        )

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
                val lngCoordinate = t * toPosition.longitude + (1 - t) * startLatLng.longitude
                val latCoordinate = t * toPosition.latitude + (1 - t) * startLatLng.latitude
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

    private fun initMaps() {
        supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
    }


    private fun callApiDirections(origin: String, destination: String) {
        showLoading()
        ApiClientMaps
            .ApiClient()
            ?.requestDirectionMaps(
                origin,
                destination,
                application.getString(R.string.google_api_key)
            )
            ?.enqueue(object : Callback<ResponseDirectionsMaps?> {
                override fun onResponse(
                    call: Call<ResponseDirectionsMaps?>,
                    response: Response<ResponseDirectionsMaps?>
                ) {
                    dismissLoading()
                    val responseDirectionsMaps = response.body()
                    if (responseDirectionsMaps?.routes?.isNotEmpty()!!) {
                        val list =
                            MapUtils.decodePoly(responseDirectionsMaps!!.routes?.get(0)?.overviewPolyline?.points)
                        val getDuration =
                            responseDirectionsMaps!!.routes?.get(0)?.legs?.get(0)?.duration?.text
                        val getKm =
                            responseDirectionsMaps.routes?.get(0)?.legs?.get(0)?.distance?.text


                        if (dataItemGetStatusCall?.status.equals("EXPIRE")!!) {
                            hideView(tvDurationEstimate)
                        } else showView(tvDurationEstimate)

                        tvDurationEstimate.text =
                            "Jarak tempuh Anda $getKm dengan estimasi sampai $getDuration"

                        addPolyline(list)
                    } else {
                        hideView(tvDurationEstimate)
                    }
                }

                override fun onFailure(call: Call<ResponseDirectionsMaps?>, t: Throwable) {
                }

            })
    }

    fun addPolyline(list: MutableList<LatLng>) {
        for (z in 0 until list.size - 1) {
            val src: LatLng = list[z]
            val dest: LatLng = list[z + 1]

            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.BLACK)
            polylineOptions.width(5f)
            polylineOptions.add(
                LatLng(src.latitude, src.longitude),
                LatLng(dest.latitude, dest.longitude)
            )
            googleMap!!.addPolyline(polylineOptions)
        }
    }


}