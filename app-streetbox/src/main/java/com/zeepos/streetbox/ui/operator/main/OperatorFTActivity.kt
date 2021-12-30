package com.zeepos.streetbox.ui.operator.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.zeepos.models.ConstVar
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.main.myparkingspace.MyParkingSpaceFragment
import com.zeepos.streetbox.ui.operator.operatortask.OperatorTaskFragment
import com.zeepos.streetbox.ui.operator.operatortaskdetail.OperatorListener
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_login.LoginActivity
import com.zeepos.utilities.SharedPreferenceUtil
import kotlinx.android.synthetic.main.operator_ft_activity.*

class OperatorFTActivity : BaseActivity<OperatorFTViewEvent, OperatorFTViewModel>(),
    OperatorListener {

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var isFlag: Boolean? = null
    var typeNotifOperator:String? = null
    var body:String? = null

//    private val typeNotifOperator: String by lazy {
//        SharedPreferenceUtil.getString(this, "title", ConstVar.EMPTY_STRING)
//            ?: ConstVar.EMPTY_STRING
//    }
//
//    private val typNotifBody: String by lazy {
//        SharedPreferenceUtil.getString(this, "body", ConstVar.EMPTY_STRING)
//            ?: ConstVar.EMPTY_STRING
//    }
    override fun initResourceLayout(): Int {
        return R.layout.operator_ft_activity
    }

    override fun onResume() {
        super.onResume()
        getLastLocation()
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(OperatorFTViewModel::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel.checkStatusShiftIn()
        getLastLocation()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG TOKEN", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                // Log and toast

                Log.d("TAG", token)

                viewModel.sendTokenFoodtruck(token!!)
            })


        typeNotifOperator = intent.getStringExtra("typeNotifOperator")
        body = intent.getStringExtra("body")


    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        toolbar.setNavigationOnClickListener{
            logout()
        }
        btn_shiftin.setOnClickListener {
            if (isFlag == false) {
                viewModel.shiftInOperatorTask()
            } else {

                    startActivity(OperatorMainActivity.getIntent(this,
                        latitude!!, longitude!!,0)
                    )


            }
        }
    }


    fun logout(){
        viewModel.logout()
        startActivity(LoginActivity.getIntent(this))
    }
    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, OperatorFTActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun onEvent(useCase: OperatorFTViewEvent) {
        when (useCase) {
            OperatorFTViewEvent.ShiftFalse ->
                saveStatusShiftFalse()

            OperatorFTViewEvent.ShiftTrue ->
                saveStatusShiftTrue()

            is OperatorFTViewEvent.GetShiftSuccess ->
                shiftInSuccess()

            is OperatorFTViewEvent.GetShiftFailed -> Toast.makeText(
                applicationContext,
                "error",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun saveStatusShiftTrue(){
        isFlag = true
        SharedPreferenceUtil.setBoolean(
            applicationContext,
            ConstVar.SHIFT,
            isFlag!!
        )
    }

    fun shiftInSuccess(){
        isFlag = true
        SharedPreferenceUtil.setBoolean(
            applicationContext,
            ConstVar.SHIFT,
            isFlag!!
        )
        startActivity(OperatorMainActivity.getIntent(this,
            0.0, 0.0,0
        ))
    }

    fun saveStatusShiftFalse(){
        isFlag = false
        SharedPreferenceUtil.setBoolean(
            applicationContext,
            ConstVar.SHIFT,
            isFlag!!
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitude = location.latitude
                        longitude = location.longitude
                        if(typeNotifOperator!=null) {
                            SharedPreferenceUtil.setString(
                                this,
                                "typeNotifOperator",
                                typeNotifOperator
                            )

                            SharedPreferenceUtil.setString(
                                this,
                                "body",
                                body
                            )
                            startActivity(OperatorMainActivity.getIntent(this,
                                latitude!!, longitude!!,0)
                            )
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
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

    override fun onFinishedCheckOut() {
        Toast.makeText(this, "tes", Toast.LENGTH_LONG).show()
    }

}



