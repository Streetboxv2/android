package com.zeepos.map.ui.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zeepos.map.R
import com.zeepos.map.ui.MapViewModel
import com.zeepos.models.ConstVar
import com.zeepos.utilities.DateTimeUtil
import kotlinx.android.synthetic.main.dialog_foodtruck_checkin.*
import java.util.*

/**
 * Created by Arif S. on 6/11/20
 */
class CheckInDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var viewModel: MapViewModel? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private var taskId: Long? = 0L
    private var typesId: Long? = 0L
    private var parkingspaceName: String? = ConstVar.EMPTY_STRING
    private var address: String? = ConstVar.EMPTY_STRING
    private var startDate: String? = ConstVar.EMPTY_STRING
    private var scheduleDate: String? = ConstVar.EMPTY_STRING
    private var endDate: String? = ConstVar.EMPTY_STRING
    private var mapType: String? = ConstVar.EMPTY_STRING
    private var isDrag: Boolean? = false
    private var types: String = ConstVar.EMPTY_STRING
    val PERMISSION_ID = 44
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.alert_dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_foodtruck_checkin, container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProvider(it).get(MapViewModel::class.java)
        }
        mFusedLocationClient =
            context?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        val args = arguments

        if (args != null) {
            taskId = args.getLong("tasksId")
            mapType = args.getString("mapType")
            isDrag = args.getBoolean("isDragable")
            parkingspaceName = args.getString("parkingSpaceName")
            currentLatitude = args.getDouble("lati")
            currentLongitude = args.getDouble("longi")
        }


        if(types=="HOMEVISIT"){
            iv_startdatetime.visibility = View.INVISIBLE
        }

        btn_checkin.setOnClickListener {
            if (mapType == ConstVar.MAP_TYPE_FREE_TASK) {
                viewModel!!.checkInOperatorFreeTask(
                    et_notes.text.toString(),
                    currentLatitude!!,
                    currentLongitude!!,
                    taskId!!
                )
            } else {
                viewModel!!.checkInOperatorTask(
                    currentLatitude!!,
                    currentLongitude!!,
                    parkingspaceName!!,
                    taskId!!,
                    typesId!!
                )
            }
        }

        if (mapType == ConstVar.MAP_TYPE_FREE_TASK && isDrag == true) {
            txt_notes.visibility = View.VISIBLE
            et_notes.visibility = View.VISIBLE
            btn_checkin.visibility = View.VISIBLE
            btn_setlocation.visibility = View.GONE
            tv_address.visibility = View.GONE
            iv_startdatetime.visibility = View.GONE
            iv_address.visibility = View.GONE
            tv_name.visibility = View.GONE
            txt_startdatetime.visibility = View.GONE
            iv_startdatetime.visibility = View.GONE

            parkingspaceName = getAddress(currentLatitude!!, currentLongitude!!)
            et_notes.setText(parkingspaceName)//address

        }

        if (mapType == ConstVar.MAP_TYPE_FREE_TASK && isDrag == false) {
            taskId = args!!.getLong("tasksId")
            txt_notes.visibility = View.VISIBLE
            et_notes.visibility = View.VISIBLE
            btn_checkin.visibility = View.VISIBLE
            btn_setlocation.visibility = View.GONE
            tv_address.visibility = View.GONE
            iv_startdatetime.visibility = View.GONE
            iv_address.visibility = View.GONE
            tv_name.visibility = View.GONE
            txt_startdatetime.visibility = View.GONE
            iv_startdatetime.visibility = View.GONE
//            getLastLocation()

            parkingspaceName = getAddress(currentLatitude!!, currentLongitude!!)
            et_notes.setText(parkingspaceName)//address
        }

        if (mapType == ConstVar.MAP_TYPE_DIRECTION) {
            taskId = args!!.getLong("taskIds")
            btn_setlocation.visibility = View.GONE
            btn_checkin.visibility = View.VISIBLE
            tv_name.visibility = View.VISIBLE
            tv_address.visibility = View.VISIBLE
            tv_address.movementMethod = ScrollingMovementMethod()
            iv_address.visibility = View.VISIBLE
            iv_startdatetime.visibility = View.VISIBLE
            txt_startdatetime.visibility = View.VISIBLE
            scheduleDate = args.getString("scheduleDate")
            startDate = args.getString("startDate")
            endDate = args.getString("endDate")
            address = args.getString("address")
            parkingspaceName = args.getString("parkingSpaceName")
            typesId = args.getLong("typesIds")
            currentLatitude = args.getDouble("lati")
            currentLongitude = args.getDouble("longi")
            types = args.getString("types")!!

            val dateSchedule = DateTimeUtil.getDateWithFormat1(
                scheduleDate!!,
                "EEEE dd-MM-yyyy"
            ) + " " + DateTimeUtil.getDateWithFormat1(
                startDate!!,
                "HH:mm"
            ) + " - " + DateTimeUtil.getDateWithFormat1(endDate!!, "HH:mm")
            tv_name.visibility = View.VISIBLE
            iv_address.visibility = View.VISIBLE
            tv_address.visibility = View.VISIBLE
            txt_startdatetime.visibility = View.VISIBLE
            txt_startdatetime.setText(dateSchedule)
            iv_startdatetime.visibility = View.VISIBLE
            txt_notes.visibility = View.GONE
            et_notes.visibility = View.GONE
            tv_name.setText(parkingspaceName)
            tv_address.setText(address)

            btn_checkin.setOnClickListener {
                if (types == "HOMEVISIT") {
                    viewModel!!.checkInOperatorTaskHomeVisit(
                        parkingspaceName!!,
                        currentLatitude!!,
                        currentLongitude!!,
                        taskId!!,
                        typesId!!
                    )
                } else {
                    viewModel!!.checkInOperatorTask(
                        currentLatitude!!,
                        currentLongitude!!,
                        parkingspaceName!!,
                        taskId!!,
                        typesId!!
                    )
                }

            }

        }


        btn_setlocation.setOnClickListener(this)
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(activity, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        )

        if (addresses.isNotEmpty()) {
            return addresses[0].getAddressLine(0)
        }
        return ""
    }

    companion object {
        fun getIntentFreeTask(
            mapType: String,
            latitude: Double,
            longitude: Double,
            parkingspaceName: String,
            tasksId: Long,
            isDrag: Boolean
        ): CheckInDialogFragment {
            val fragment = CheckInDialogFragment()
//            fragment.isCancelable = false
            val args = Bundle()
            args!!.putString("mapType", mapType)
            args!!.putDouble("lati", latitude!!);
            args!!.putDouble("longi", longitude);
            args!!.putString("parkingSpaceName", parkingspaceName);
            args!!.putLong("tasksId", tasksId!!)
            args!!.putBoolean("isDragable", isDrag)
            fragment.arguments = args
            return fragment
        }

        fun getIntentRegular(
            mapType: String,
            latitude: Double,
            longitude: Double,
            taskId: Long,
            typesId: Long,
            startDate: String,
            endDate: String,
            scheduleDate: String,
            address: String,
            parkingspaceName: String,
            types: String
        ): CheckInDialogFragment {
            val fragment = CheckInDialogFragment()
            fragment.isCancelable = false
            val args = Bundle()
            args!!.putString("mapType", mapType)
            args!!.putDouble("lati", latitude!!)
            args!!.putDouble("longi", longitude)
            args!!.putLong("taskIds", taskId)
            args!!.putLong("typesIds", typesId)
            args!!.putString("startDate", startDate)
            args!!.putString("endDate", endDate)
            args!!.putString("scheduleDate", scheduleDate)
            args!!.putString("address", address)
            args!!.putString("parkingSpaceName", parkingspaceName)
            args!!.putString("types", types)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onClick(v: View?) {
        val args = arguments
        if (mapType == ConstVar.MAP_TYPE_FREE_TASK) {
            taskId = args!!.getLong("tasksId")
            getLastLocation()

        } else {
            currentLatitude = args!!.getDouble("lati")
            currentLongitude = args.getDouble("longi")
            address = args.getString("address")
            parkingspaceName = args.getString("parkingSpaceName")
            types = args.getString("types")!!
            taskId = args!!.getLong("taskIds")
            typesId = args!!.getLong("typesIds")
            if (types == "HOMEVISIT") {
                viewModel!!.checkInOperatorTaskHomeVisit(
                    parkingspaceName!!,
                    currentLatitude!!,
                    currentLongitude!!,
                    taskId!!,
                    typesId!!
                )
            } else {
                viewModel!!.checkInOperatorTask(
                    currentLatitude!!,
                    currentLongitude!!,
                    parkingspaceName!!,
                    taskId!!,
                    typesId!!
                )
            }
        }

    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                activity?.let {
                    mFusedLocationClient.lastLocation.addOnCompleteListener(it) { task ->
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            currentLatitude = location.latitude
                            currentLongitude = location.longitude
                            val addresses: List<Address>
                            val geocoder = Geocoder(context, Locale.getDefault())

                            addresses = geocoder.getFromLocation(
                                currentLatitude!!,
                                currentLongitude!!,
                                1
                            )
                            parkingspaceName =
                                addresses[0].getAddressLine(0) + " " + et_notes.text.toString()
//                             parkingspaceName = et_notes.text.toString()
                            if (isDrag == true) {
                                txt_notes.visibility = View.VISIBLE
                                et_notes.visibility = View.VISIBLE

                            }

                            et_notes.setText(parkingspaceName)
                            btn_setlocation.visibility = View.GONE
                            btn_checkin.visibility = View.VISIBLE

                            btn_checkin.setOnClickListener {
                                viewModel!!.checkInOperatorFreeTask(
                                    et_notes.text.toString()!!,
                                    currentLatitude!!,
                                    currentLongitude!!,
                                    taskId!!
                                )
                            }


                        }
                    }
                }
            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient =
            context?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            Log.d("LOCATION", (mLastLocation.latitude + mLastLocation.longitude).toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED &&
            activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_ID
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

}