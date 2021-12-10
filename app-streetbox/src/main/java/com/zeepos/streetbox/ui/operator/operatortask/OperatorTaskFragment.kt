package com.zeepos.streetbox.ui.operator.operatortask

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingWorkPolicy
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.zeepos.map.ui.MapActivity
import com.zeepos.models.ConstVar
import com.zeepos.models.response.ResponseDirectionsMaps
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.networkmaps.ApiClientMaps
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.operator.main.OperatorFTActivity
import com.zeepos.streetbox.ui.operator.main.OperatorMainActivity
import com.zeepos.streetbox.worker.LocationUpdateWorker
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.ui_login.LoginActivity
import com.zeepos.utilities.*
import kotlinx.android.synthetic.main.operator_task_fragment.*
import kotlinx.android.synthetic.main.operator_task_fragment.swipe_refresh
import kotlinx.android.synthetic.main.parkingspace_fragment.*
import kotlinx.android.synthetic.main.parkingspace_fragment.rcv
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class OperatorTaskFragment : BaseFragment<OperatorTaskViewEvent, OperatorTaskViewModel>() {

    private lateinit var operatorTaskAdapter: OperatorTaskAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLatLng: LatLng? = null
    private var locationCallback: LocationCallback? = null
    private var latParkingSpace: Double = 0.0
    private var lonParkingSpace: Double = 0.0
    private var parkingOperatorId: Long? = null
    private var status: Int? = null
    private var address: String? = ConstVar.EMPTY_STRING
    private var startDate: String? = ConstVar.EMPTY_STRING
    private var parkingSpaceName: String? = ConstVar.EMPTY_STRING
    private var endDate: String? = ConstVar.EMPTY_STRING
    private var typesId: Long? = 0L
    private var scheduleDate: String?= ConstVar.EMPTY_STRING
    private var isFlag : Boolean = false
    private var customerName: String = ConstVar.EMPTY_STRING
    private var types: String = ConstVar.EMPTY_STRING
    private var parkingSpace: String = ConstVar.EMPTY_STRING
    private var listLatLng:MutableList<LatLng> = ArrayList()

    override fun initResourceLayout(): Int {
        return R.layout.operator_task_fragment
    }

    override fun init() {
        context?.let { fusedLocationProviderClient = FusedLocationProviderClient(it)}
        context?.let { requestCurrentLocation(it) }

        viewModel =
            ViewModelProvider(this, viewModeFactory).get(OperatorTaskViewModel::class.java)

        operatorTaskAdapter = OperatorTaskAdapter()
        val arg = arguments


    }

    override fun onResume() {
        showLoading()
        viewModel.getParkingOperatorTask()
        super.onResume()
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

    override fun onViewReady(savedInstanceState: Bundle?) {
        rcv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = operatorTaskAdapter
        }

        swipe_refresh.setColorSchemeColors(Color.rgb(47, 223, 189))
        swipe_refresh.isRefreshing = true
        swipe_refresh.setOnRefreshListener {
            viewModel.getParkingOperatorTask()
        }

        btn_shiftout.setOnClickListener {
            viewModel.shiftOutOperatorTask()
            saveStatusShiftFalse()
        }

        operatorTaskAdapter.setOnItemChildClickListener { adapter, _, position ->
            context?.let {
                val parkingOperator = adapter.getItem(position) as TaskOperator
                status = parkingOperator.status
                parkingOperatorId = parkingOperator.tasksId
                typesId = parkingOperator.typesId
                address = parkingOperator.address
                parkingSpaceName = parkingOperator.name
                types = parkingOperator.types!!

                if (status == 3) {
                    if(types == "HOMEVISIT"){
                        viewModel.checkOutOperatorTaskHomeVisit(  parkingSpaceName!!,
                            currentLatLng!!.latitude,
                            currentLatLng!!.longitude,
                            parkingOperatorId!!,
                            typesId!!)
                    }else {
                        viewModel.checkOutOperatorTask(
                            currentLatLng!!.latitude,
                            currentLatLng!!.longitude,
                            parkingSpaceName!!,
                            parkingOperatorId!!,
                            typesId!!
                        )
                    }
                } else {
                    Toast.makeText(context, "You Have to Check In", Toast.LENGTH_LONG).show()
                }
            }
        }

        operatorTaskAdapter.setOnItemClickListener { adapter, _, position ->
            context?.let {
                val parkingOperator = adapter.getItem(position) as TaskOperator

                parkingOperatorId = parkingOperator.tasksId
                status = parkingOperator.status
                address = parkingOperator.address
                startDate = parkingOperator.startDate
                endDate = parkingOperator.endDate
                parkingSpaceName = parkingOperator.name
                typesId = parkingOperator.typesId
                scheduleDate = parkingOperator.scheduleDate
                types = parkingOperator.types!!
                latParkingSpace = parkingOperator.latParkingSpace
                lonParkingSpace = parkingOperator.lonParkingSpace


                if (status == 3) {
                    Toast.makeText(context, "You already checked in this task", Toast.LENGTH_LONG)
                        .show()
                } else if(!DateTimeUtil.getDateWithFormat1(scheduleDate!!,"dd-MM-yyyy").equals(DateTimeUtil.getCurrentTimeStamp("dd-MM-yyyy"))){
                   /* Toast.makeText(context, "You are not allowed to start this task yet", Toast.LENGTH_LONG)
                        .show()*/
                    viewModel.taskOnGoing(parkingOperatorId!!)
                    goToMap()
                }else{
                    viewModel.taskOnGoing(parkingOperatorId!!)
                    var origin:String = ""
                    var destination:String = ""
                    origin = "${currentLatLng!!.latitude},${currentLatLng!!.longitude}"
                    destination = "${latParkingSpace},${lonParkingSpace}"
                    hitGoogleMap(origin,destination)
                }
            }
        }

    }

    override fun onEvent(useCase: OperatorTaskViewEvent) {
        when (useCase) {
            is OperatorTaskViewEvent.GetAllParkingOperatorTaskSuccess -> {
                if (swipe_refresh.isRefreshing) {
                    operatorTaskAdapter.data.clear()

                }
                operatorTaskAdapter.setList(useCase.data)
                if (useCase.data.size == 0) {
                    btn_shiftout.visibility = View.VISIBLE
                    checkDataSizeEmpty("")
                }

                swipe_refresh.isRefreshing = false
                dismissLoading()
            }
            is OperatorTaskViewEvent.GetAllParkingOperatorTaskFailed -> {
                if(useCase.errorMessage.contains("401")){
                    viewModel.deleteSession()
                    startActivity(activity?.let { LoginActivity.getIntent(it) })
                }
                dismissLoading()
                Toast.makeText(context, useCase.errorMessage, Toast.LENGTH_SHORT).show()
            }

            is OperatorTaskViewEvent.GetShiftOutSuccess -> {
                startActivity(activity?.let { OperatorFTActivity.getIntent(it) })
            }

            is OperatorTaskViewEvent.GetShiftOutFailed -> {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }

            is OperatorTaskViewEvent.GetTaskOnGoingSuccess -> Log.d("success","success")

            is OperatorTaskViewEvent.GetTaskOnGoingFailed -> Log.d("ongoing","failed")

            is OperatorTaskViewEvent.GetCheckOutSuccess ->
                startActivity(activity?.let { OperatorMainActivity.getIntent(it,0.0,0.0,0) })

            is OperatorTaskViewEvent.GetCheckOutFailed ->
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            is OperatorTaskViewEvent.GetCheckOutHomeVisitSuccess -> startActivity(activity?.let { OperatorMainActivity.getIntent(it,0.0,0.0,0) })

            is OperatorTaskViewEvent.GetCheckOutHomeVisitFailed -> Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveStatusShiftFalse(){
        isFlag = false
        context?.let {
            SharedPreferenceUtil.setBoolean(
                it,
                ConstVar.SHIFT,
                isFlag
            )
        }
    }

    @SuppressLint("NewApi")
    fun checkDataSizeDate(startDate:String){
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-mm-yyyy")
        val now: LocalDateTime = LocalDateTime.now()
        if(startDate != now.toString()) {
            context?.let {
                SharedPreferenceUtil.setInt(
                    it,
                    "size",
                    0
                )
            }
        }

    }

    fun checkDataSizeEmpty(date: String){
            context?.let {
                SharedPreferenceUtil.setString(
                    it,
                    "date",
                    date
                )
            }


    }

    fun hitGoogleMap(origin: String, destination: String){
            showLoading()
            ApiClientMaps
                .ApiClient()
                ?.requestDirectionMaps(
                    origin,
                    destination,
                    context!!.getString(R.string.google_api_key)
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
                            listLatLng = list
                        }
                        goToMap()
                    }

                    override fun onFailure(call: Call<ResponseDirectionsMaps?>, t: Throwable) {
                    }

                })
        }


    fun goToMap(){
        if(types == "REGULAR"){
            context?.let {
                LocationUpdateWorker.enqueue(it, ExistingWorkPolicy.REPLACE, parkingOperatorId!!)
            }
        }

        startActivity(
            activity?.let { it1 ->
                MapActivity.getIntent(
                    it1,
                    type = ConstVar.MAP_TYPE_DIRECTION,
                    taskId = parkingOperatorId!!,
                    typesId = typesId!!,
                    address = address!!,
                    startDate = startDate!!,
                    endDate = endDate!!,
                    scheduleDate = scheduleDate!!,
                    parkingSpaceName = parkingSpaceName!!,
                    types = types,
                    latLng = listLatLng
                )
            }
        )
    }

    companion object {

        fun getInstance(data: Bundle?): OperatorTaskFragment {
            val fragment = OperatorTaskFragment()
            fragment.arguments = data
            return fragment
        }
        fun newInstance(): OperatorTaskFragment {
            return OperatorTaskFragment()
        }
    }
}