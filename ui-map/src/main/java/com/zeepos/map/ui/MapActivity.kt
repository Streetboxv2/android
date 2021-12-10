package com.zeepos.map.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.zeepos.domain.interactor.map.UpdateCurrentLocationToCloud
import com.zeepos.map.R
import com.zeepos.map.ui.dialogs.AddressDisplayBottomSheet
import com.zeepos.map.ui.dialogs.CheckInDialogFragment
import com.zeepos.map.ui.dialogs.merchantinfo.MerchantInfoDialogFragment
import com.zeepos.map.ui.dialogs.merchantinfo.MerchantInfoNonRegDialog
import com.zeepos.map.ui.dialogs.merchantinfo.ParkingSpaceInfoDialogFragment
import com.zeepos.map.utils.AnimationUtils
import com.zeepos.map.utils.MapUtils
import com.zeepos.map.utils.PermissionUtils
import com.zeepos.map.utils.ViewUtils
import com.zeepos.map.view.MerchantCheckInInfoWindow
import com.zeepos.map.workers.LocationUpdateWorker
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.MapData
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.ParkingSpace
import com.zeepos.models.response.ResponseDirectionsMaps
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.SharedPreferenceUtil
import com.zeepos.utilities.hideView
import com.zeepos.utilities.showView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_maps.*
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Arif S. on 5/21/20
 */
class MapActivity : BaseActivity<MapViewEvent, MapViewModel>(), CheckInListener,
    GoogleMap.OnMarkerDragListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    private var isTripEnd: Boolean = false
    private lateinit var oldPosition: LatLng
    var distance: String? = ""

    var latLng: LatLng? = null
    var listlatlng:MutableList<LatLng> = ArrayList()

    @Inject
    lateinit var mapUiEvent: MapUiEvent

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var updateCurrentLocationToCloud: UpdateCurrentLocationToCloud
    private var compositeDisposable = CompositeDisposable()

    private val appType: String by lazy {
        SharedPreferenceUtil.getString(this, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
            ?: ConstVar.EMPTY_STRING
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
        private const val PICKUP_REQUEST_CODE = 1
        private const val DROP_REQUEST_CODE = 2

        fun getIntent(
            context: Context,
            parkingSpaceId: Long = 0L,
            type: String,
            taskId: Long = 0L,
            typesId: Long = 0L,
            address: String = ConstVar.EMPTY_STRING,
            startDate: String = ConstVar.EMPTY_STRING,
            endDate: String = ConstVar.EMPTY_STRING,
            scheduleDate: String = ConstVar.EMPTY_STRING,
            parkingSpaceName: String = ConstVar.EMPTY_STRING,
            types: String = ConstVar.EMPTY_STRING,
            latLng:MutableList<LatLng> = ArrayList(),
            bundle: Bundle? = null
        ): Intent {
            val intent = Intent(context, MapActivity::class.java)
            intent.putExtra("parkingSpaceId", parkingSpaceId)
            intent.putExtra("type", type)
            intent.putExtra("taskId", taskId)
            intent.putExtra("typesId", typesId)
            intent.putExtra("address", address)
            intent.putExtra("startDate", startDate)
            intent.putExtra("endDate", endDate)
            intent.putExtra("scheduleDate", scheduleDate)
            intent.putExtra("parkingSpaceName", parkingSpaceName)
            intent.putParcelableArrayListExtra("listlatlng",ArrayList(latLng))
            intent.putExtra("types", types)

            if (bundle != null)
                intent.putExtras(bundle)
            return intent
        }
    }

    private lateinit var googleMap: GoogleMap
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback
    private var currentLatLng: LatLng? = null
    private var pickUpLatLng: LatLng? = null
    private var dropLatLng: LatLng? = null
    private val nearbyFoodTruckMarkerMap = hashMapOf<Marker, Any>()
    private var destinationMarker: Marker? = null
    private var originMarker: Marker? = null
    private var greyPolyLineList = arrayListOf<Polyline>()
    private var orangePolyLineList = arrayListOf<Polyline>()
    private var previousLatLngFromServerMap = hashMapOf<Any, LatLng>()
    private var currentLatLngFromServerMap = hashMapOf<Any, LatLng>()
    private var movingFoodTruckMarkerMap = hashMapOf<Marker, Any>()
    private var movingFoodTruckMarkerInfoMap = hashMapOf<Marker, Marker>()
    private var mapType: String = ConstVar.MAP_TYPE_LIVE_TRACK
    private var parkingSalesId: Long = 0L
    private var taskId: Long = 0L
    private var typesId: Long = 0L
    private var latparkingspace:Double = 0.0
    private var lonparkingspace:Double = 0.0
    private var address: String? = null
    private var startdate: String? = null
    private var enddate: String? = null
    private var parkingspaceName: String? = ConstVar.EMPTY_STRING
    private var parkingspaceNameRegular: String? = ConstVar.EMPTY_STRING
    private var currentLat: Double? = null
    private var currentLong: Double? = null
    private var scheduleDate: String? = ConstVar.EMPTY_STRING
    private var foodTruckId: Long = 0
    private var types: String? = ConstVar.EMPTY_STRING

    override fun initResourceLayout(): Int {
        return R.layout.activity_maps
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(MapViewModel::class.java)
        parkingSalesId = intent.getLongExtra("parkingSpaceId", 0L)
        taskId = intent.getLongExtra("taskId", 0L)
        typesId = intent.getLongExtra("typesId", 0L)
        mapType = intent.getStringExtra("type")!!
        address = intent.getStringExtra("address")
        startdate = intent.getStringExtra("startDate")
        enddate = intent.getStringExtra("endDate")
        scheduleDate = intent.getStringExtra("scheduleDate")
        parkingspaceNameRegular = intent.getStringExtra("parkingSpaceName")
        types = intent.getStringExtra("types")
        listlatlng = intent.getParcelableArrayListExtra("listlatlng")
        latparkingspace = listlatlng[0].latitude
        lonparkingspace = listlatlng[0].longitude
        Log.d("address", "" + address)
        Log.d("startdate", "" + startdate)
        Log.d("enddate", "" + enddate)

        val bundle = intent.extras
        if (bundle != null) {
            if (mapType == ConstVar.MAP_TYPE_LOCATION) {
                val lat = bundle.getDouble("lat")
                val lng = bundle.getDouble("lng")
                currentLatLng = LatLng(lat, lng)
            } else if (mapType == ConstVar.MAP_TYPE_LIVE_TRACK || mapType == ConstVar.MAP_TYPE_CHECK_IN_LOCATION) {
                taskId = bundle.getLong("taskId")
                types = bundle.getString("types")
            }
        }

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        ViewUtils.enableTransparentStatusBar(window)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            oldPosition = map.cameraPosition.target
            initMapAction()
        }

        if (mapType == ConstVar.MAP_TYPE_NEAR_BY || mapType == ConstVar.MAP_TYPE_LIVE_TRACK ||
            mapType == ConstVar.MAP_TYPE_DIRECTION || mapType == ConstVar.MAP_TYPE_LOCATION ||
            mapType == ConstVar.MAP_TYPE_FREE_TASK || mapType == ConstVar.MAP_TYPE_CHECK_IN_LOCATION
        ) {
            pickUpDropLayout.visibility = View.GONE
        }

        if (mapType == ConstVar.MAP_TYPE_DIRECTION) {

            btnCancel.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                viewModel.undo(taskId)
            }
        }

        if (mapType == ConstVar.MAP_TYPE_FREE_TASK) {
//            setUpLocationListener()
        }

//        setUpClickListener()

        if (mapType != ConstVar.MAP_TYPE_PICK_LOCATION) {
            icon_pick_location_marker.visibility = View.GONE
            icon_marker_shadow.visibility = View.GONE
            cl_search_location.visibility = View.GONE
        } else {
            tv_search_location.setOnClickListener {
                launchLocationAutoCompleteActivity(PICKUP_REQUEST_CODE)
            }
        }
    }

    override fun onEvent(useCase: MapViewEvent) {
        when (useCase) {
            is MapViewEvent.GetSuccessDistanceKm -> {

                if (useCase.dataDistance.data != null) {
                    distance = useCase.dataDistance.data!!.value
                    latLng?.let { viewModel.requestNearbyFoodTruck(it, distance.toString()) }
                }
            }

            is MapViewEvent.ShowNearbyFoodTrucks -> {
                showNearbyFoodTrucks(useCase.mapDataList)

                currentLatLng?.let {
                    viewModel.scheduledGetAllNearByLatestLocationFromServer(
                        it.latitude,
                        it.longitude
                    )
                }
            }
            MapViewEvent.InformCabBooked -> informCabBooked()
            is MapViewEvent.ShowPath -> {
                showPath(useCase.mapData)

            }
            is MapViewEvent.UpdateFoodTruckLocation ->{
                updateFoodTruckLocation(useCase.mapData)
            }
            MapViewEvent.InformFoodTruckIsArriving -> informFoodTruckIsArriving()
            MapViewEvent.InformFoodTruckArrived -> informFoodTruckArrived()
            MapViewEvent.InformTripStart -> informTripStart()
            MapViewEvent.InformTripEnd -> informTripEnd()
            MapViewEvent.ShowRoutesNotAvailableError -> showRoutesNotAvailableError()
            is MapViewEvent.ShowDirectionApiFailedError -> showDirectionApiFailedError(useCase.error)
            is MapViewEvent.GetCheckInSuccess -> {
                LocationUpdateWorker.stop(this)
                mapUiEvent.goToOperatorMainActivity(
                    this,
                    currentLatLng!!.latitude!!,
                    currentLatLng!!.longitude!!,
                    0
                )
            }
            is MapViewEvent.GetCheckInFailed ->
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT)
                    .show()
            MapViewEvent.InformLiveTrackStart -> informLiveTrackStart()
            is MapViewEvent.GetAllParkingOperatorTaskSuccess -> {
            }
            is MapViewEvent.GetAllParkingOperatorTaskFailed -> {
            }
            is MapViewEvent.GoToMerchantMenuScreen ->
                mapUiEvent.goToMerchantMenuScreen(
                    this,
                    useCase.merchantId, useCase.bundle
                )
            is MapViewEvent.GetCheckInFreeTaskSuccess ->
                mapUiEvent.goToOperatorMainActivity(
                    this,
                    currentLatLng!!.latitude!!,
                    currentLatLng!!.longitude!!,
                    1
                )
            is MapViewEvent.GetCheckInFreeTaskFailed -> Toast.makeText(
                this,
                "failed",
                Toast.LENGTH_LONG
            ).show()
            is MapViewEvent.UndoSuccess ->
                mapUiEvent.goToOperatorMainActivity(
                    this,
                    currentLatLng!!.latitude!!,
                    currentLatLng!!.longitude!!,
                    0
                )

            is MapViewEvent.UndoFailed -> Toast.makeText(
                this,
                "failed undo",
                Toast.LENGTH_LONG
            ).show()
            is MapViewEvent.GetCheckInHomeVisitSuccess -> {
                mapUiEvent.goToOperatorMainActivity(
                    this,
                    currentLatLng!!.latitude!!,
                    currentLatLng!!.longitude!!,
                    0
                )
            }
            is MapViewEvent.GetCheckInHomeVisitFailed -> {
                Toast.makeText(
                    this,
                    "failed undo",
                    Toast.LENGTH_LONG
                )
            }
            is MapViewEvent.OnSelectedAddress -> {
                val resultIntent = Intent()
                resultIntent.putExtra("address", useCase.address.address)
                resultIntent.putExtra("latitude", useCase.address.latitude)
                resultIntent.putExtra("longitude", useCase.address.longitude)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun initMapAction() {
        when (mapType) {
            ConstVar.MAP_TYPE_LIVE_TRACK -> {
                informLiveTrackStart()
            }
            ConstVar.MAP_TYPE_CHECK_IN_LOCATION -> {
                viewModel.getCurrentFoodTruckCheckInLocationFromServer(taskId)
            }
            ConstVar.MAP_TYPE_DIRECTION -> {


            }
            ConstVar.MAP_TYPE_LOCATION -> {
                currentLatLng?.let {
                    addMarkerAndGet(it, R.drawable.ic_parking_space)
                    moveCamera(it)
                    animateCamera(it)
                }
            }

        }

        googleMap.setOnMarkerDragListener(this)
        googleMap.setOnCameraMoveListener(this)
        googleMap.setOnCameraIdleListener(this)
        googleMap.setOnCameraMoveStartedListener {

            if (mapType == ConstVar.MAP_TYPE_PICK_LOCATION) {
                icon_pick_location_marker.animate().translationY(-50f).start()
                icon_marker_shadow.animate().withStartAction {
                    icon_marker_shadow.setPadding(5, 5, 5, 5)
                }.start()
            }
        }

        googleMap.setOnMarkerClickListener {
            val data = movingFoodTruckMarkerMap[it]
            if (data != null && data is TaskOperator) {
                if (it.isInfoWindowShown) {
                    it.hideInfoWindow()
                } else {
                    it.showInfoWindow()
                }
            }

            val mapData = nearbyFoodTruckMarkerMap[it]
            val movingFoodTruck = movingFoodTruckMarkerMap[it]

            if (mapData != null && mapData is MapData) {
                val merchant = mapData.exData
                if (merchant is FoodTruck) {
                    when (merchant.types) {
                        ConstVar.FOODTRUCK_TYPE_REGULAR -> {
                            val bundle = Bundle()
                            bundle.putLong("merchantId", merchant.merchantId)
                            bundle.putString("name", merchant.name)
                            bundle.putString("address", merchant.address)
                            bundle.putString("logo", merchant.logo)
                            bundle.putString("merchantIg", merchant.merchantIG)
                            bundle.putLong("typesId", merchant.typesId)
                            bundle.putString("foodTruckData", gson.toJson(merchant))

                            val merchantInfo = MerchantInfoDialogFragment()
                            merchantInfo.arguments = bundle
                            showDialog(merchantInfo, bundle = bundle)
                        }
//                        ConstVar.FOODTRUCK_TYPE_HOMEVISIT -> {
//                            val bundle = Bundle()
//                            bundle.putString("foodTruckData", gson.toJson(merchant))
//                            val dialog = MerchantViewMenuDialog()
//                            dialog.arguments = bundle
//                            showDialog(dialog, bundle = bundle)
//                        }
                        else -> {
//                            val bundle = Bundle()
//                            bundle.putString("foodTruckData", gson.toJson(merchant))
//                            val dialog = MerchantViewMenuDialog()
//                            dialog.arguments = bundle
//                            showDialog(dialog, bundle = bundle)

                            val bundle = Bundle()
                            bundle.putLong("merchantId", merchant.merchantId)
                            bundle.putString("name", merchant.name)
                            bundle.putString("address", merchant.address)
                            bundle.putString("logo", merchant.logo)
                            bundle.putString("merchantIg", merchant.merchantIG)
                            bundle.putLong("typesId", merchant.typesId)
                            bundle.putString("foodTruckData", gson.toJson(merchant))

//                            val merchantInfo = MerchantInfoDialogFragment()
                            val merchantInfo = MerchantInfoNonRegDialog()
                            merchantInfo.arguments = bundle
                            showDialog(merchantInfo, bundle = bundle)
                        }
                    }
                } else if (merchant is ParkingSpace) {
                    val bundle = Bundle()
                    bundle.putLong("parkingSpaceId", merchant.id)
                    bundle.putString("name", merchant.name)
                    bundle.putString("address", merchant.address)

                    val merchantInfo = ParkingSpaceInfoDialogFragment()
                    merchantInfo.arguments = bundle
                    showDialog(merchantInfo, bundle = bundle)
                }
            } else if (movingFoodTruck != null && movingFoodTruck is TaskOperator) {
                val merchant = FoodTruck()
                merchant.merchantId = movingFoodTruck.merchantId
                merchant.merchantName = movingFoodTruck.merchantName
                merchant.name = movingFoodTruck.merchantName
                merchant.address = movingFoodTruck.address
                merchant.logo = movingFoodTruck.logo
                merchant.merchantIG = movingFoodTruck.merchantIG
                merchant.typesId = movingFoodTruck.typesId
                merchant.status = movingFoodTruck.status
                merchant.tasksId = movingFoodTruck.tasksId
                merchant.banner = movingFoodTruck.banner

                val bundle = Bundle()
                bundle.putLong("merchantId", merchant.merchantId)
                bundle.putString("name", merchant.name)
                bundle.putString("address", merchant.address)
                bundle.putString("logo", merchant.logo)
                bundle.putString("merchantIg", merchant.merchantIG)
                bundle.putLong("typesId", merchant.typesId)
                bundle.putString("foodTruckData", gson.toJson(merchant))

                val merchantInfo = MerchantInfoNonRegDialog()
                merchantInfo.arguments = bundle
                showDialog(merchantInfo, bundle = bundle)
            } else {
                var moveFT: TaskOperator? = null
                movingFoodTruckMarkerInfoMap.forEach { markerInfoEntry ->
                    if (markerInfoEntry.value == it) {
                        moveFT = movingFoodTruckMarkerMap[markerInfoEntry.key] as TaskOperator
                        return@forEach
                    }
                }

                if (moveFT != null) {
                    val movingFoodTruck2 = moveFT!!
                    val merchant = FoodTruck()
                    merchant.merchantId = movingFoodTruck2.merchantId
                    merchant.merchantName = movingFoodTruck2.merchantName
                    merchant.name = movingFoodTruck2.merchantName
                    merchant.address = movingFoodTruck2.address
                    merchant.logo = movingFoodTruck2.logo
                    merchant.merchantIG = movingFoodTruck2.merchantIG
                    merchant.typesId = movingFoodTruck2.typesId
                    merchant.status = movingFoodTruck2.status
                    merchant.tasksId = movingFoodTruck2.tasksId
                    merchant.banner = movingFoodTruck2.banner

                    val bundle = Bundle()
                    bundle.putLong("merchantId", merchant.merchantId)
                    bundle.putString("name", merchant.name)
                    bundle.putString("address", merchant.address)
                    bundle.putString("logo", merchant.logo)
                    bundle.putString("merchantIg", merchant.merchantIG)
                    bundle.putLong("typesId", merchant.typesId)
                    bundle.putString("foodTruckData", gson.toJson(merchant))

                    val merchantInfo = MerchantInfoNonRegDialog()
                    merchantInfo.arguments = bundle
                    showDialog(merchantInfo, bundle = bundle)
                }
            }
            true
        }
    }

    private fun setUpClickListener() {
        pickUpTextView.setOnClickListener {
            launchLocationAutoCompleteActivity(PICKUP_REQUEST_CODE)
        }
        dropTextView.setOnClickListener {
            launchLocationAutoCompleteActivity(DROP_REQUEST_CODE)
        }
        requestCabButton.setOnClickListener {
            statusTextView.visibility = View.VISIBLE
            statusTextView.text = getString(R.string.requesting_your_cab)
            requestCabButton.isEnabled = false
            pickUpTextView.isEnabled = false
            dropTextView.isEnabled = false
        }

    }

    private fun launchLocationAutoCompleteActivity(requestCode: Int) {
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(this)
        startActivityForResult(intent, requestCode)
    }

    private fun moveCamera(latLng: LatLng?) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addMerchantMarkerAndGet(
        latLng: LatLng,
        imageBackgroundDrawable: Int,
        logo: Bitmap
    ): Marker {

        val bmpBackground = BitmapFactory.decodeResource(resources, imageBackgroundDrawable)
        val strokeWidth = 8
        val width = bmpBackground.width - (strokeWidth * 2)
        val height = bmpBackground.height - 45
        val bmOverlay: Bitmap =
            Bitmap.createBitmap(bmpBackground.width, bmpBackground.height, bmpBackground.config)
        val scaledBitmap = Bitmap.createScaledBitmap(logo, width, height, false)
        val canvas = Canvas(bmOverlay)

        canvas.drawBitmap(bmpBackground, Matrix(), null)
        canvas.drawBitmap(scaledBitmap, strokeWidth.toFloat(), strokeWidth.toFloat(), null)

        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmOverlay)

        val infoWindowAdapter = MerchantCheckInInfoWindow(this, FoodTruck())
        googleMap.setInfoWindowAdapter(infoWindowAdapter)

        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun addMerchantOngoingMarkerAndGet(latLng: LatLng, bitmap: Bitmap? = null): Marker {

        val bmp = bitmap ?: BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_merchant_logo_default
        )

        val view: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.marker_ongoing,
                null
            )
        val ivLogo = view.findViewById<ImageView>(R.id.iv_logo)
        val scaledBitmap =
            Bitmap.createScaledBitmap(bmp, 100, 100, false)
        ivLogo.setImageBitmap(getRoundedCornerBitmap(scaledBitmap))
//        ivLogo.setImageBitmap(scaledBitmap)

        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(createDrawableFromView(view))

        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun addMerchantCheckInMarkerAndGet(latLng: LatLng, bitmap: Bitmap? = null): Marker {

        val bmp = bitmap ?: BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_merchant_logo_default
        )

        val view: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.marker_check_in,
                null
            )
        val ivLogo = view.findViewById<ImageView>(R.id.iv_logo)
        val scaledBitmap =
            Bitmap.createScaledBitmap(bmp, 100, 100, false)
        ivLogo.setImageBitmap(getRoundedCornerBitmap(scaledBitmap))
//        ivLogo.setImageBitmap(scaledBitmap)

        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(createDrawableFromView(view))

        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun addMerchantCheckOutMarkerAndGet(latLng: LatLng, bitmap: Bitmap): Marker {
        val view: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.marker_check_out,
                null
            )
        val ivLogo = view.findViewById<ImageView>(R.id.iv_logo)
        val scaledBitmap =
            Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        ivLogo.setImageBitmap(getRoundedCornerBitmap(scaledBitmap))
//        ivLogo.setImageBitmap(scaledBitmap)

        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(createDrawableFromView(view))

        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun createDrawableFromView(view: View): Bitmap? {
        view.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.width, view.height)
        view.draw(canvas)
        return bitmap
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
//        val color = 0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = 12f
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
//        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    private fun addMarkerAndGetDrag(latLng: LatLng, imageDrawable: Int): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            MapUtils.getBitmap(
                this,
                imageDrawable
            )
        )
        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor).draggable(true)
        )
    }

    private fun addMarkerAndGet(latLng: LatLng, imageDrawable: Int): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            MapUtils.getBitmap(
                this,
                imageDrawable
            )
        )
        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun addFoodTruckMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            MapUtils.getBitmap(
                this,
                R.drawable.food_truck_marker
            )
        )
        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun addOriginPickupMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getPickupDestinationBitmap())
        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getDestinationBitmap())
        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun goToCheckInFreeTask() {
        if (mapType == ConstVar.MAP_TYPE_FREE_TASK) {
            showDialog(
                CheckInDialogFragment.getIntentFreeTask(
                    ConstVar.MAP_TYPE_FREE_TASK,
                    currentLat!!,
                    currentLong!!,
                    parkingspaceName!!,
                    taskId!!,
                    false
                )
            )
        }
    }

    private fun setCurrentLocationAsPickUp() {
        pickUpLatLng = currentLatLng
        pickUpTextView.text = getString(R.string.current_location)
        tv_search_location.text = getString(R.string.current_location)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocationOnMap() {
        googleMap.setPadding(0, ViewUtils.dpToPx(48f), 0, 0)
        googleMap.isMyLocationEnabled = true
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
    private fun initLocation() {
        setCurrentLocationAsPickUp()
//        enableMyLocationOnMap()
        moveCamera(currentLatLng)
        animateCamera(currentLatLng)

        currentLatLng?.let {
            latLng = it
            when (mapType) {
                ConstVar.MAP_TYPE_DIRECTION -> {
                    addPolyline(listlatlng)

//                    viewModel.requestDirection(
//                        taskId,
//                        it.latitude,
//                        it.longitude,
//                        latparkingspace ,
//                        lonparkingspace
//                    )



//                    LocationUpdateWorker.enqueue(
//                        this@MapActivity,
//                        ExistingWorkPolicy.REPLACE,
//                        taskId,
//                        updateCurrentLocationToCloud
//                    )
                }
                ConstVar.MAP_TYPE_FREE_TASK -> {
                    val addresses: List<Address>
                    val geoCoder = Geocoder(applicationContext, Locale.getDefault())

                    addresses = geoCoder.getFromLocation(
                        it.latitude,
                        it.longitude,
                        1
                    )
                    currentLat = it.latitude
                    currentLong = it.longitude

                    if (addresses.isNotEmpty()) {
                        parkingspaceName = addresses[0].getAddressLine(0)
                        goToCheckInFreeTask()
                    }

                    addMarkerAndGetDrag(it, R.drawable.ic_parking_space)
                }
                ConstVar.MAP_TYPE_FREE_TASK_DIRECTION -> {
                    viewModel.updateFreeTaskCurrentFoodTruckPosition(
                        taskId,
                        it.latitude,
                        it.longitude
                    )
                }
                ConstVar.MAP_TYPE_PICK_LOCATION -> {

                }
                else -> {
                    viewModel.getDistanceKm()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {

        fusedLocationProviderClient = FusedLocationProviderClient(this)
        // for getting the current location update after every 5 seconds
        val locationRequest =
            LocationRequest().setInterval(5000).setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (currentLatLng == null) {

                    for (loc in locationResult.locations) {
                        if (currentLatLng == null) {
                            currentLatLng =
                                LatLng(loc.latitude, loc.longitude)
                        }
                    }

                    initLocation()

                } else {

                    if (mapType == ConstVar.MAP_TYPE_DIRECTION) {

                        for (loc in locationResult.locations) {
                            currentLatLng =
                                LatLng(loc.latitude, loc.longitude)
                        }

                        currentLatLng?.let {

//                            tv_log.text =
//                                "${tv_log.text} lat/lng -> ${it.latitude} / ${it.longitude}\n"
//                            Log.d("current position", tv_log.text.toString())

                            if (!isTripEnd) {
                                viewModel.updateCurrentFoodTruckPosition(
                                    taskId,
                                    it.latitude,
                                    it.longitude,
                                    latparkingspace,
                                    lonparkingspace
                                )
                            }
                        }
                    } else if (mapType == ConstVar.MAP_TYPE_FREE_TASK_DIRECTION) {

                        for (loc in locationResult.locations) {
                            currentLatLng =
                                LatLng(loc.latitude, loc.longitude)
                        }

                        currentLatLng?.let {

//                            tv_log.text =
//                                "${tv_log.text} lat/lng -> ${it.latitude} / ${it.longitude}\n"
//                            Log.d("current position", tv_log.text.toString())

                            if (!isTripEnd) {
                                viewModel.updateFreeTaskCurrentFoodTruckPosition(
                                    taskId,
                                    it.latitude,
                                    it.longitude
                                )
                            }
                        }
                    }

                }
            }
        }

        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun checkAndShowRequestButton() {
        if (pickUpLatLng !== null && dropLatLng !== null) {
            requestCabButton.visibility = View.VISIBLE
            requestCabButton.isEnabled = true
        }
    }

    private fun reset() {
        statusTextView.visibility = View.GONE
        btnCancel.visibility = View.GONE
        nearbyFoodTruckMarkerMap.forEach { it.key.remove() }
        nearbyFoodTruckMarkerMap.clear()
        previousLatLngFromServerMap.clear()
        currentLatLngFromServerMap.clear()
        if (currentLatLng != null) {
            moveCamera(currentLatLng)
            animateCamera(currentLatLng)
            setCurrentLocationAsPickUp()
            viewModel.getDistanceKm()
            latLng = currentLatLng
        } else {
            pickUpTextView.text = ""
            tv_search_location.text = ""
        }
        pickUpTextView.isEnabled = true
        dropTextView.isEnabled = true
        dropTextView.text = ""
        movingFoodTruckMarkerMap.forEach { it.key.remove() }
        movingFoodTruckMarkerInfoMap.forEach { it.key.remove() }
        greyPolyLineList.forEach { it.remove() }
        orangePolyLineList.forEach { it.remove() }
        originMarker?.remove()
        destinationMarker?.remove()
        dropLatLng = null
        greyPolyLineList.clear()
        orangePolyLineList.clear()
        originMarker = null
        destinationMarker = null
        movingFoodTruckMarkerMap.clear()
        movingFoodTruckMarkerInfoMap.clear()
    }

    override fun onStart() {
        super.onStart()
        if (mapType == ConstVar.MAP_TYPE_LIVE_TRACK || mapType == ConstVar.MAP_TYPE_CHECK_IN_LOCATION) return
        if (currentLatLng == null) {
            when {
                PermissionUtils.isAccessFineLocationGranted(this) -> {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                }
                else -> {
                    PermissionUtils.requestAccessFineLocationPermission(
                        this,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKUP_REQUEST_CODE || requestCode == DROP_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.d(
                        ConstVar.TAG,
                        "Place: " + place.name + ", " + place.id + ", " + place.latLng
                    )
                    when (requestCode) {
                        PICKUP_REQUEST_CODE -> {
                            pickUpTextView.text = place.name
                            tv_search_location.text = place.address
                            pickUpLatLng = place.latLng
                            checkAndShowRequestButton()

                            if (mapType == ConstVar.MAP_TYPE_PICK_LOCATION) {
                                animateCamera(pickUpLatLng)
                            }
                        }
                        DROP_REQUEST_CODE -> {
                            dropTextView.text = place.name
                            dropLatLng = place.latLng
                            checkAndShowRequestButton()
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Log.d(ConstVar.TAG, status.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> {
                    Log.d(ConstVar.TAG, "Place Selection Canceled")
                }
            }
        }
    }

    private fun showNearbyFoodTrucks(mapDataList: List<MapData>) {
        nearbyFoodTruckMarkerMap.clear()

        for (mapData in mapDataList) {
            val lat = mapData.latLng?.lat ?: 0.0
            val lng = mapData.latLng?.lng ?: 0.0
            val latLng = LatLng(lat, lng)
            val exData = mapData.exData

            if (exData is FoodTruck) {
                val type = exData.types ?: ConstVar.EMPTY_STRING

                if (type == ConstVar.FOODTRUCK_TYPE_HOMEVISIT) {
                    when (exData.status) {
                        ConstVar.FOOD_TRUCK_STATUS_CHECK_IN -> {
                            val resource = BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_merchant_logo_default
                            )

                            val marker = addMerchantCheckInMarkerAndGet(
                                latLng,
                                resource
                            )

                            nearbyFoodTruckMarkerMap[marker] = mapData

                        }
                        ConstVar.FOOD_TRUCK_STATUS_CHECK_OUT -> {

                            val resource = BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_merchant_logo_default
                            )

                            val marker = addMerchantCheckOutMarkerAndGet(
                                latLng,
                                resource
                            )

                            nearbyFoodTruckMarkerMap[marker] = mapData

                        }
                    }
                } else {

                    when (exData.status) {
                        ConstVar.FOOD_TRUCK_STATUS_CHECK_IN -> {
                            val logo = ConstVar.PATH_IMAGE + exData.logo

                            GlideApp.with(this)
                                .asBitmap()
                                .load(logo)
                                .error(R.drawable.ic_merchant_logo_default)
                                .override(100, 100)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        Log.d(ConstVar.TAG, "image loaded")
                                    }

                                    override fun onLoadFailed(errorDrawable: Drawable?) {
                                        val resource = BitmapFactory.decodeResource(
                                            resources,
                                            R.drawable.ic_merchant_logo_default
                                        )
                                        val marker = addMerchantCheckInMarkerAndGet(
                                            latLng,
                                            resource
                                        )

                                        nearbyFoodTruckMarkerMap[marker] = mapData
                                    }

                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {

                                        val marker = addMerchantCheckInMarkerAndGet(
                                            latLng,
                                            resource
                                        )

                                        nearbyFoodTruckMarkerMap[marker] = mapData
                                    }

                                })
                        }
                        ConstVar.FOOD_TRUCK_STATUS_CHECK_OUT -> {
                            val logo = ConstVar.PATH_IMAGE + exData.logo

                            GlideApp.with(this)
                                .asBitmap()
                                .load(logo)
                                .error(R.drawable.ic_merchant_logo_default)
                                .override(100, 100)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        Log.d(ConstVar.TAG, "image loaded")
                                    }

                                    override fun onLoadFailed(errorDrawable: Drawable?) {
                                        val resource = BitmapFactory.decodeResource(
                                            resources,
                                            R.drawable.ic_merchant_logo_default
                                        )
                                        val marker = addMerchantCheckOutMarkerAndGet(
                                            latLng,
                                            resource
                                        )

                                        nearbyFoodTruckMarkerMap[marker] = mapData
                                    }

                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {

                                        val marker = addMerchantCheckOutMarkerAndGet(
                                            latLng,
                                            resource
                                        )

                                        nearbyFoodTruckMarkerMap[marker] = mapData
                                    }

                                })
                        }

                    }
                }

            } else if (exData is ParkingSpace) {
                val nearbyParkingMarker = if (exData.isCheckin) {
                    addMarkerAndGet(latLng, R.drawable.ic_parking_space)
                } else {
                    addMarkerAndGet(latLng, R.drawable.ic_parking_grey)
                }

                nearbyFoodTruckMarkerMap[nearbyParkingMarker] = mapData
            }
        }

        Log.d(ConstVar.TAG, "marker data -> ${nearbyFoodTruckMarkerMap.size}")
    }

    private fun informCabBooked() {
        nearbyFoodTruckMarkerMap.forEach { it.key.remove() }
        nearbyFoodTruckMarkerMap.clear()
        requestCabButton.visibility = View.GONE
        statusTextView.text = getString(R.string.your_cab_is_booked)
    }


    private fun showPath(mapData: MapData) {

        val latLngList = arrayListOf<LatLng>()
        val builder = LatLngBounds.Builder()
        for (latLngData in mapData.latLngList) {
            val latLng = LatLng(latLngData.lat, latLngData.lng)
            builder.include(latLng)
            latLngList.add(latLng)
        }
        val bounds = builder.build()

        moveCamera(latLngList[0])
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))
        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.GRAY)
        polylineOptions.width(5f)
        polylineOptions.addAll(latLngList)
        val greyPolyLine = googleMap.addPolyline(polylineOptions)
        greyPolyLineList.add(greyPolyLine)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(5f)
        blackPolylineOptions.color(Color.parseColor("#EF6623"))
        val orangePolyLine = googleMap.addPolyline(blackPolylineOptions)
        orangePolyLineList.add(orangePolyLine)

        originMarker = addOriginPickupMarkerAndGet(latLngList[0])
        originMarker?.setAnchor(0.5f, 0.5f)

        destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
        destinationMarker?.setAnchor(0.5f, 0.5f)

        val polylineAnimator = AnimationUtils.polyLineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (greyPolyLine.points!!.size * (percentValue / 100.0f)).toInt()
            orangePolyLine.points = greyPolyLine.points!!.subList(0, index)
        }
        polylineAnimator.start()
    }

    private fun updateFoodTruckLocation(mapData: MapData) {
        var movingFoodTruckMarker: Marker? = null
        var movingFoodTruckMarkerInfo: Marker? = null

        if (mapData.exData == null) return

        val newTaskOperator = mapData.exData as TaskOperator
        val lat = mapData.latLng?.lat ?: 0.0
        val lng = mapData.latLng?.lng ?: 0.0
        val newLatLng = LatLng(lat, lng)


            movingFoodTruckMarkerMap.forEach {
                val taskOperator = it.value as TaskOperator
                if (taskOperator.tasksId == newTaskOperator.tasksId) {
                    movingFoodTruckMarker = it.key

                    if (appType == ConstVar.APP_CUSTOMER) {
                        movingFoodTruckMarkerInfo = movingFoodTruckMarkerInfoMap[it.key]
                    }
                    return@forEach
                }

        }

        if (movingFoodTruckMarker == null) {
            val marker = addFoodTruckMarkerAndGet(newLatLng)
            movingFoodTruckMarkerMap[marker] = newTaskOperator
            movingFoodTruckMarker = marker

            if (appType == ConstVar.APP_CUSTOMER) {

                val logoPath = "${ConstVar.PATH_IMAGE}${newTaskOperator.logo}"

                GlideApp.with(this)
                    .asBitmap()
                    .load(logoPath)
                    .error(R.drawable.ic_merchant_logo_default)
                    .override(100, 100)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            Log.d(ConstVar.TAG, "image loaded")
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            val resource = BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_merchant_logo_default
                            )
                            val markerInfo = addMerchantOngoingMarkerAndGet(
                                newLatLng,
                                resource
                            )

                            movingFoodTruckMarkerInfoMap[marker] = markerInfo
                            movingFoodTruckMarkerInfo = markerInfo

                            animateMarker(
                                movingFoodTruckMarker,
                                newLatLng,
                                newTaskOperator,
                                movingFoodTruckMarkerInfo
                            )
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {

                            val markerInfo = addMerchantOngoingMarkerAndGet(
                                newLatLng,
                                resource
                            )

                            movingFoodTruckMarkerInfoMap[marker] = markerInfo
                            movingFoodTruckMarkerInfo = markerInfo

                            animateMarker(
                                movingFoodTruckMarker,
                                newLatLng,
                                newTaskOperator,
                                movingFoodTruckMarkerInfo
                            )
                        }

                    })
            }else{
                animateMarker(
                    movingFoodTruckMarker,
                    newLatLng,
                    newTaskOperator,
                    movingFoodTruckMarkerInfo
                )
            }

        } else {
            animateMarker(
                movingFoodTruckMarker,
                newLatLng,
                newTaskOperator,
                movingFoodTruckMarkerInfo
            )
        }

    }

    private fun animateMarker(
        movingFoodTruckMarker: Marker?, newLatLng: LatLng,
        newTaskOperator: TaskOperator, movingFoodTruckMarkerInfo: Marker?
    ) {

        var currentLatLngFromServer: LatLng? = null
        var previousLatLngFromServer: LatLng? = null

        if (previousLatLngFromServerMap.containsKey(newTaskOperator.tasksId)) {
            previousLatLngFromServer = previousLatLngFromServerMap[newTaskOperator.tasksId]
        }

        if (currentLatLngFromServerMap.containsKey(newTaskOperator.tasksId)) {
            currentLatLngFromServer = currentLatLngFromServerMap[newTaskOperator.tasksId]
        }

        if (previousLatLngFromServer == null) {
            currentLatLngFromServerMap[newTaskOperator.tasksId] = newLatLng
            previousLatLngFromServerMap[newTaskOperator.tasksId] = newLatLng
            movingFoodTruckMarker?.position = newLatLng
            movingFoodTruckMarker?.setAnchor(0.5f, 0.5f)

            if (appType == ConstVar.APP_CUSTOMER) {
                movingFoodTruckMarkerInfo?.position = newLatLng
                movingFoodTruckMarkerInfo?.setAnchor(0.5f, 1f)
            }

            if (mapType != ConstVar.MAP_TYPE_NEAR_BY)
                animateCamera(newLatLng)

            if (mapType == ConstVar.MAP_TYPE_LIVE_TRACK)
                viewModel.getCurrentTaskDirection(
                    newTaskOperator.tasksId,
                    newLatLng.latitude,
                    newLatLng.longitude,
                    latparkingspace,
                    lonparkingspace
                )
        } else {
            if (previousLatLngFromServer == newLatLng) return

            previousLatLngFromServerMap[newTaskOperator.tasksId] = currentLatLngFromServer!!
            previousLatLngFromServer = currentLatLngFromServer
            currentLatLngFromServer = newLatLng
            currentLatLngFromServerMap[newTaskOperator.tasksId] = newLatLng

            if (mapType == ConstVar.MAP_TYPE_DIRECTION) {
                //dummy comment this on production!!
                //this is for simulate moving operator
//                currentLatLng = LatLng(newLatLng.latitude, newLatLng.longitude)
            }

            val valueAnimator = AnimationUtils.cabAnimator()

            valueAnimator.addUpdateListener { va ->
                val multiplier = va.animatedFraction
                val nextLocation = LatLng(
                    multiplier * currentLatLngFromServer.latitude + (1 - multiplier) * previousLatLngFromServer.latitude,
                    multiplier * currentLatLngFromServer.longitude + (1 - multiplier) * previousLatLngFromServer.longitude
                )

                movingFoodTruckMarker?.position = nextLocation
                movingFoodTruckMarker?.setAnchor(0.5f, 0.5f)
                val rotation = MapUtils.getRotation(previousLatLngFromServer, nextLocation)

                if (!rotation.isNaN()) {
                    movingFoodTruckMarker?.rotation = rotation
                }
//                    animateCamera(nextLocation)
            }
            valueAnimator.start()

            if (appType == ConstVar.APP_CUSTOMER) {
                val valueAnimatorInfo = AnimationUtils.cabAnimator()

                valueAnimatorInfo.addUpdateListener { va ->
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLatLngFromServer.latitude + (1 - multiplier) * previousLatLngFromServer.latitude,
                        multiplier * currentLatLngFromServer.longitude + (1 - multiplier) * previousLatLngFromServer.longitude
                    )

                    movingFoodTruckMarkerInfo?.position = nextLocation
                    movingFoodTruckMarkerInfo?.setAnchor(0.5f, 1f)
                }
                valueAnimatorInfo.start()
            }
        }
    }

    private fun informFoodTruckIsArriving() {
        statusTextView.text = getString(R.string.your_cab_is_arriving)
    }

    private fun informFoodTruckArrived() {
        greyPolyLineList.forEach { it.remove() }
        orangePolyLineList.forEach { it.remove() }
        originMarker?.remove()
        destinationMarker?.remove()
    }

    private fun informTripStart() {
        statusTextView.text = getString(R.string.you_are_on_a_trip)
        previousLatLngFromServerMap.clear()
    }

    private fun informLiveTrackStart() {
        previousLatLngFromServerMap.clear()
        viewModel.scheduledGetLatestLocationFromServer(taskId)
    }

    private fun informTripEnd() {
        btnCancel.visibility = View.INVISIBLE
        if (mapType == ConstVar.MAP_TYPE_DIRECTION) {
            showDialog(
                CheckInDialogFragment.getIntentRegular(
                    ConstVar.MAP_TYPE_DIRECTION,
                    currentLatLng!!.latitude,
                    currentLatLng!!.longitude,
                    taskId,
                    typesId,
                    startdate!!,
                    enddate!!,
                    scheduleDate!!,
                    address!!,
                    parkingspaceNameRegular!!,
                    types!!
                )
            )
        }
        greyPolyLineList.forEach { it.remove() }
        orangePolyLineList.forEach { it.remove() }
        originMarker?.remove()
        destinationMarker?.remove()
        isTripEnd = true
    }

    private fun showRoutesNotAvailableError() {
        val error = getString(R.string.route_not_available_choose_different_locations)
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        reset()
    }

    private fun showDirectionApiFailedError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        reset()
    }

    override fun onFinishedCheckedIn() {
        reset()
    }

    override fun onMarkerDragEnd(marker: Marker?) {
        if (marker == null) return
        if (mapType == ConstVar.MAP_TYPE_FREE_TASK) {
            showDialog(
                CheckInDialogFragment.getIntentFreeTask(
                    ConstVar.MAP_TYPE_FREE_TASK,
                    marker.position.latitude,
                    marker.position.longitude,
                    parkingspaceName!!,
                    taskId!!,
                    true
                )
            )
        }
    }

    override fun onMarkerDragStart(marker: Marker?) {
        if (marker == null) return
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.position));
    }

    override fun onMarkerDrag(p0: Marker?) {
        Log.i("System out", "onMarkerDrag...");
    }

    override fun onCameraMove() {

    }

    override fun onCameraIdle() {
        val currentScreen = googleMap.projection.visibleRegion.latLngBounds
        compositeDisposable.dispose()

        val checkInMarkerDis = Observable.fromIterable(movingFoodTruckMarkerMap.keys)
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (currentScreen.contains(it.position))
                    it.showInfoWindow()
            }

        compositeDisposable.add(checkInMarkerDis)

        if (mapType == ConstVar.MAP_TYPE_PICK_LOCATION) {
            val newPosition = googleMap.cameraPosition.target
            if (newPosition != oldPosition) {
                icon_pick_location_marker.animate().translationY(0f).start()
                icon_marker_shadow.animate().withStartAction {
                    icon_marker_shadow.setPadding(0, 0, 0, 0)
                }.start()
            }

            showDialog(
                AddressDisplayBottomSheet.getInstance(
                    newPosition.latitude,
                    newPosition.longitude
                )
            )
        }
    }

}