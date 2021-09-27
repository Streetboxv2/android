package com.zeepos.streetbox.ui.operatorfreetask

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingWorkPolicy
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.zeepos.map.ui.MapActivity
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.FoodTruckLocation
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.operator.main.OperatorFTActivity
import com.zeepos.streetbox.ui.operator.main.OperatorMainActivity
import com.zeepos.streetbox.worker.LocationUpdateWorker
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.PermissionUtils
import com.zeepos.utilities.SharedPreferenceUtil
import kotlinx.android.synthetic.main.fragment_operator_free_task.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class OperatorFreeTaskFragment :
    BaseFragment<OperatorFreeTaskViewEvent, OperatorFreeTaskViewModel>() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationCallback2: LocationCallback? = null
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0
    private var tasksId: Long = 0L
    private var address: String = ConstVar.EMPTY_STRING
    private var isClicked: Boolean = false
    private var currentLatLng: LatLng? = null

    private val getTaskId: String by lazy {
        context?.let { SharedPreferenceUtil.getString(it, "tasksId", "0") }
            ?: "0"
    }

    private val getDate: String by lazy {
        context?.let { SharedPreferenceUtil.getString(it, "date", "") }
            ?: ""
    }

    private var isFoodTruckVisible = true

    override fun initResourceLayout(): Int {
        return R.layout.fragment_operator_free_task
    }

    override fun init() {
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(OperatorFreeTaskViewModel::class.java)

        context?.let {
            isFoodTruckVisible = SharedPreferenceUtil.getBoolean(it, "visibility", true)
            fusedLocationProviderClient = FusedLocationProviderClient(it)
        }

        viewModel.getStatusNonRegulerTask()

    }

    @SuppressLint("MissingPermission")
    override fun onViewReady(savedInstanceState: Bundle?) {
//        tv_logs.movementMethod = ScrollingMovementMethod()

        btn_start.setOnClickListener {
            val curretDateTime: String = DateTimeUtil.getCurrentTimeStamp("dd-MM-yyyy")!!
            if (getDate.isEmpty() || !getDate.equals(curretDateTime)) {

                context?.let {
                    val alertDialogBuilder = AlertDialog.Builder(it)
                    alertDialogBuilder.setMessage("By starting free task you will be visible in customers")
                    alertDialogBuilder.setPositiveButton(
                        "Visible"
                    ) { p0, _ ->
                        p0?.dismiss()
                        isClicked = true
                        isFoodTruckVisible = true
                        showLoading()
                        SharedPreferenceUtil.getBoolean(it, "visibility", isFoodTruckVisible)
                        viewModel.createOperatorFreeTask()
                    }
                    alertDialogBuilder.setNegativeButton(
                        "Hide"
                    ) { p0, _ ->
                        p0?.dismiss()
                        isFoodTruckVisible = false
                        SharedPreferenceUtil.getBoolean(it, "visibility", isFoodTruckVisible)
                        viewModel.createOperatorFreeTask()
                    }

                    alertDialogBuilder.show()
                }

            } else {
                Toast.makeText(
                    context,
                    "Please complete your reguler task(s)",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        btn_check_in.setOnClickListener {
            showLoading()
            context?.let {
                requestCurrentLocation(it)
                LocationUpdateWorker.stop(it)
            }
        }

        btn_checkOut.setOnClickListener {
            context?.let {
                val alertDialogBuilder = AlertDialog.Builder(it)
                alertDialogBuilder.setMessage("Checkout current location")
                alertDialogBuilder.setPositiveButton(
                    "Check out"
                ) { p0, _ ->
                    p0?.dismiss()
                    showLoading()

                    context?.let {context ->
                        getLastLocation(context)
                    }
                }
                alertDialogBuilder.setNegativeButton(
                    "Cancel"
                ) { p0, _ ->
                    p0?.dismiss()
                }

                alertDialogBuilder.show()
            }
        }

        btn_shiftoutfreetask.setOnClickListener {
            viewModel.shiftOutOperatorTask()
        }

        btn_show_map_location.setOnClickListener {
            //this lat lng must get from server!!
            context?.let {
                LocationServices.getFusedLocationProviderClient(it)
                    ?.lastLocation?.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val bundle = Bundle()
                            bundle.putDouble("lat", location.latitude)
                            bundle.putDouble("lng", location.longitude)
                            startActivity(
                                MapActivity.getIntent(
                                    it,
                                    type = ConstVar.MAP_TYPE_LOCATION,
                                    bundle = bundle
                                )
                            )
                        }
                    }
            }
        }

        btn_direction.setOnClickListener {
            context?.let {
                if (isFoodTruckVisible) {
                    LocationUpdateWorker.enqueue(it, ExistingWorkPolicy.REPLACE, tasksId)
                } else {
                    LocationUpdateWorker.stop(it)
                }
            }

            startActivity(
                activity?.let { it1 ->
                    MapActivity.getIntent(
                        it1,
                        type = ConstVar.MAP_TYPE_FREE_TASK_DIRECTION,
                        taskId = tasksId
                    )
                }
            )
        }

    }

    private fun showCurrentAddress(context: Context, latLng: LatLng) {
        val addresses: List<Address>
        val geocoder = Geocoder(context, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            1
        )

        if (addresses.isNotEmpty()) {
            val address: String = addresses[0]
                .getAddressLine(0)
            val city: String = addresses[0].locality
            val state: String = addresses[0].adminArea
            val country: String = addresses[0].countryName
            val postalCode: String = addresses[0].postalCode
            val knownName: String = addresses[0].featureName

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Check in current address?")
            alertDialogBuilder.setMessage(address)
            alertDialogBuilder.setPositiveButton(
                "Yes"
            ) { p0, _ ->
                p0?.dismiss()
                tv_address?.text = address

                viewModel.checkInOperatorFreeTask(
                    address,
                    latLng.latitude,
                    latLng.longitude,
                    tasksId
                )
            }
            alertDialogBuilder.setNegativeButton(
                "Select in map"
            ) { p0, _ ->
                p0?.dismiss()
                goToMap(tasksId)
            }

            alertDialogBuilder.show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(context: Context) {
        if (PermissionUtils.isLocationEnabled(context)) {
            // for getting the current location update after every 5 seconds
            val locationRequest =
                LocationRequest().setInterval(5000).setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            locationCallback2?.let {
                fusedLocationProviderClient.removeLocationUpdates(it)
            }

            locationCallback2 = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    var mLocation: Location? = null

                    for (loc in locationResult.locations) {
                        mLocation = loc
                    }

                    if (mLocation != null) {

                        latitude = currentLatLng?.latitude ?: 0.0
                        longitude = currentLatLng?.longitude ?: 0.0

                        if (latitude!! <= 0)
                            latitude = mLocation.latitude

                        if (longitude!! <= 0)
                            longitude = mLocation.longitude

                        if (tasksId <= 0)
                            tasksId = getTaskId.toLong()

                        viewModel.checkOutOperatorFreeTask(
                            tv_address.text.toString(),
                            mLocation.latitude,
                            mLocation.longitude,
                            tasksId
                        )

                        fusedLocationProviderClient.removeLocationUpdates(
                            locationCallback2
                        )
                    }
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback2,
                Looper.myLooper()
            )
        } else {
            PermissionUtils.showGPSNotEnabledDialog(context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation(context: Context) {

        if (PermissionUtils.isLocationEnabled(context)) {
            // for getting the current location update after every 5 seconds
            val locationRequest =
                LocationRequest().setInterval(5000).setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            locationCallback?.let {
                fusedLocationProviderClient.removeLocationUpdates(it)
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    var mLocation: Location? = null

                    for (loc in locationResult.locations) {
                        mLocation = loc
                    }

                    if (mLocation != null) {
                        dismissLoading()

                        currentLatLng = LatLng(mLocation.latitude, mLocation.longitude)

                        showCurrentAddress(context, currentLatLng!!)

                        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    }
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
            )
        } else {
            PermissionUtils.showGPSNotEnabledDialog(context)
        }
    }

    override fun onEvent(useCase: OperatorFreeTaskViewEvent) {
        dismissLoading()

        when (useCase) {
            is OperatorFreeTaskViewEvent.GetStatusNonRegulerSuccess -> {
                tasksId = useCase.data.id

                currentLatLng = LatLng(useCase.data.latitude, useCase.data.longitude)

                when {
                    useCase.data.status == ConstVar.TASK_STATUS_IN_PROGRESS -> {
                        btn_check_in.visibility = View.VISIBLE
                        btn_start.visibility = View.GONE
                        btn_checkOut.visibility = View.GONE
                        btn_direction.visibility = View.VISIBLE
                        btn_show_map_location.visibility = View.GONE

                        context?.let {
                            if (isFoodTruckVisible) {
                                LocationUpdateWorker.enqueue(
                                    it,
                                    ExistingWorkPolicy.REPLACE,
                                    tasksId
                                )
                            } else {
                                LocationUpdateWorker.stop(it)
                            }
                        }
                    }
                    useCase.data.status > ConstVar.TASK_STATUS_IN_PROGRESS -> {
                        btn_check_in.visibility = View.GONE
                        btn_start.visibility = View.GONE
                        btn_checkOut.visibility = View.VISIBLE
                        btn_direction.visibility = View.GONE
                        btn_show_map_location.visibility = View.VISIBLE

                        context?.let {
                            LocationUpdateWorker.stop(it)
                        }
                    }
                    else -> {
                        btn_check_in.visibility = View.GONE
                        btn_start.visibility = View.VISIBLE
                        btn_checkOut.visibility = View.GONE
                        btn_direction.visibility = View.GONE
                        btn_show_map_location.visibility = View.GONE
                    }
                }

                GotoParkingOperatorFreeTask(tasksId)
            }

            is OperatorFreeTaskViewEvent.GetStatusNonRegulerFailed -> {
//                if (isClicked)
//                    viewModel.createOperatorFreeTask()

                btn_check_in.visibility = View.GONE
                btn_start.visibility = View.VISIBLE
                btn_checkOut.visibility = View.GONE
                btn_direction.visibility = View.GONE
                btn_show_map_location.visibility = View.GONE

            }

            is OperatorFreeTaskViewEvent.GetAllParkingOperatorFreeTaskSuccess -> {
                dismissLoading()
                getOnGoing(
                    useCase.data.address!!,
                    useCase.data.latParkingSpace,
                    useCase.data.lonParkingSpace,
                    tasksId
                )

            }

            is OperatorFreeTaskViewEvent.GetAllParkingOperatorFreeTaskFailed -> {
//                if (isClicked)
//                    viewModel.createOperatorFreeTask()
            }
            is OperatorFreeTaskViewEvent.GetCreateFreeTaskSuccess -> {
                tasksId = useCase.data.id
                when {
                    useCase.data.status.toInt() == ConstVar.TASK_STATUS_IN_PROGRESS -> {
                        btn_check_in.visibility = View.VISIBLE
                        btn_start.visibility = View.GONE
                        btn_checkOut.visibility = View.GONE
                        btn_direction.visibility = View.VISIBLE
                        btn_show_map_location.visibility = View.GONE
                    }
                    useCase.data.status > ConstVar.TASK_STATUS_IN_PROGRESS -> {
                        btn_check_in.visibility = View.GONE
                        btn_start.visibility = View.GONE
                        btn_checkOut.visibility = View.VISIBLE
                        btn_direction.visibility = View.GONE
                        btn_show_map_location.visibility = View.VISIBLE
                    }
                    else -> {
                        btn_check_in.visibility = View.GONE
                        btn_start.visibility = View.VISIBLE
                        btn_checkOut.visibility = View.GONE
                        btn_direction.visibility = View.GONE
                        btn_show_map_location.visibility = View.GONE
                    }
                }

                context?.let {
                    if (isFoodTruckVisible) {
                        LocationUpdateWorker.enqueue(it, ExistingWorkPolicy.REPLACE, tasksId)
                    } else {
                        LocationUpdateWorker.stop(it)
                    }
                }
            }

            is OperatorFreeTaskViewEvent.GetCreateFreeTaskFailed ->
                Toast.makeText(activity, "failed", Toast.LENGTH_LONG).show()

            is OperatorFreeTaskViewEvent.GetTaskOnGoingFailed ->
                Toast.makeText(activity, "failed checkout", Toast.LENGTH_LONG).show()

            is OperatorFreeTaskViewEvent.GetCheckOutFreeTaskSuccess -> {
                dismissLoading()
                reset()
            }

            is OperatorFreeTaskViewEvent.GetShiftOutSuccess ->
                startActivity(activity?.let { OperatorFTActivity.getIntent(it) })

            is OperatorFreeTaskViewEvent.GetShiftOutFailed ->
                Toast.makeText(activity, "failed shiftout", Toast.LENGTH_LONG).show()
            is OperatorFreeTaskViewEvent.GetTaskOnGoingSuccess -> {
            }

            is OperatorFreeTaskViewEvent.GetCheckOutFreeTaskFailed -> {
                dismissLoading()
            }
            is OperatorFreeTaskViewEvent.GetCheckInFreeTaskSuccess -> {
                btn_check_in.visibility = View.GONE
                btn_start.visibility = View.GONE
                btn_checkOut.visibility = View.VISIBLE
                btn_direction.visibility = View.GONE
                btn_show_map_location.visibility = View.VISIBLE
            }
            is OperatorFreeTaskViewEvent.GetCheckInFreeTaskFailed -> {
                btn_check_in.visibility = View.VISIBLE
                btn_start.visibility = View.GONE
                btn_checkOut.visibility = View.GONE
                btn_direction.visibility = View.VISIBLE
                btn_show_map_location.visibility = View.GONE
            }
        }
    }

    fun reset() {
        btn_checkOut.visibility = View.GONE
        btn_start.visibility = View.VISIBLE
        btn_direction.visibility = View.GONE
        btn_show_map_location.visibility = View.GONE
        tv_address.text = ConstVar.BUKALAPAK
        context?.let { SharedPreferenceUtil.setString(it, "tasksId", "0") }

        startActivity(context?.let {
            OperatorMainActivity.getIntent(
                it, latitude!!, longitude!!, 1
            )

        })
    }

    fun GotoParkingOperatorFreeTask(tasksId: Long) {
        context?.let {
            SharedPreferenceUtil.setString(
                it,
                "tasksId",
                tasksId.toString()
            )
        }
        viewModel.getParkingOperatorFreeTask()
    }

    fun getOnGoing(address: String, latitude: Double, longitude: Double, tasksId: Long) {
        if (address == null || address == "") {
            tv_address.text = ConstVar.BUKALAPAK
            btn_checkOut.visibility = View.GONE
            btn_shiftoutfreetask.visibility = View.VISIBLE
            btn_start.visibility = View.GONE
            btn_check_in.visibility = View.VISIBLE
            btn_direction.visibility = View.VISIBLE
            btn_show_map_location.visibility = View.GONE

            if (isClicked) {
//                goToMap(tasksId)
                context?.let {
                    if (isFoodTruckVisible) {
                        LocationUpdateWorker.enqueue(it, ExistingWorkPolicy.APPEND, tasksId)
                    } else {
                        LocationUpdateWorker.stop(it)
                    }
                }
            }
        } else {
            tv_address.text = address
            btn_checkOut.visibility = View.VISIBLE
            btn_start.visibility = View.GONE
            btn_check_in.visibility = View.GONE
            btn_direction.visibility = View.GONE
            btn_show_map_location.visibility = View.VISIBLE

            context?.let {
                LocationUpdateWorker.stop(it)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FoodTruckLocation) {
//        tv_logs.text =
//            "${tv_logs.text} ${event.tag} : taskId -> ${event.taskId}, lat/lng -> ${event.latitude} / ${event.longitude}\n"
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    fun goToMap(tasksId: Long) {
        startActivity(
            activity?.let { it1 ->
                MapActivity.getIntent(
                    it1,
                    type = ConstVar.MAP_TYPE_FREE_TASK,
                    taskId = tasksId,
                    address = "",
                    startDate = "",
                    endDate = "",
                    scheduleDate = ""

                )
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun createFreeTask() {
        tv_address.setText(ConstVar.BUKALAPAK)
        viewModel.createOperatorFreeTask()
    }

    companion object {
        fun newInstance(): OperatorFreeTaskFragment {
            return OperatorFreeTaskFragment()
        }
    }
}