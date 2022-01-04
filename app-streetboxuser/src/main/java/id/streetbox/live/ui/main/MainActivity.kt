package id.streetbox.live.ui.main

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.zeepos.map.utils.PermissionUtils
import com.zeepos.ui_base.ui.BaseActivity
import id.streetbox.live.R
import id.streetbox.live.ui.main.cart.CartFragment
import id.streetbox.live.ui.main.doortodoor.DoortoDoorFragment
import id.streetbox.live.ui.main.home.HomeFragment
import id.streetbox.live.ui.main.orderhistory.OrderHistoryFragment
import id.streetbox.live.ui.main.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewEvent, MainViewModel>() {

    private var doubleBackToExitPressedOnce = false
    var typeNotif: String? = null

    override fun initResourceLayout(): Int {
        return R.layout.activity_main
    }


    override fun init() {
        typeNotif = intent.getStringExtra("typeNotif")
        println("respon notif main $typeNotif")
        viewModel = ViewModelProvider(this, viewModeFactory).get(MainViewModel::class.java)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG TOKEN", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                // Log and toast

                Log.d("TAG", "$token")

                if (token != null)
                    viewModel.sendToken(token)
            })


//        clearNotification()

    }


    fun clearNotification() {
        val notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        if (typeNotif != null) {
//            addFragment(
               /* HomeFragment.newInstance(typeNotif.toString()),
                R.id.fl_content,
                HomeFragment::class.simpleName*/
            if(typeNotif!!.equals("Online Order Nearby")){
                replaceFragment(
                    OrderHistoryFragment.newInstance(),
                    R.id.fl_content,
                    OrderHistoryFragment::class.simpleName
                )
            }
//            else{
//                replaceFragment(
//                    DoortoDoorFragment.newInstance("",""),
//                    R.id.fl_content,
//                    OrderHistoryFragment::class.simpleName
//                )
//            }

//            )
        } else {
            addFragment(
                HomeFragment.newInstance(""),
                R.id.fl_content,
                HomeFragment::class.simpleName
            )
        }
        nav_view.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }

    override fun onEvent(useCase: MainViewEvent) {

    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        com.zeepos.utilities.PermissionUtils.isReadWriteStoragePermissionGranted(
                            this
                        )
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
                            com.zeepos.utilities.PermissionUtils.isReadWriteStoragePermissionGranted(
                                this
                            )
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(com.zeepos.map.R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            com.zeepos.utilities.PermissionUtils.RC_READ_WRITE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    com.zeepos.utilities.PermissionUtils.isReadWriteStoragePermissionGranted(this)
                    Toast.makeText(
                        this, "Storage Permission not granted",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        var locationCallback: LocationCallback? = null
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.location.postValue(location)
                } else {//sometime location can be null so use this function to get location every 2 sec.
                    val fusedLocationProviderClient = FusedLocationProviderClient(this)
                    // for getting the current location update after every 5 seconds
                    val locationRequest =
                        LocationRequest().setInterval(2000).setFastestInterval(2000)
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)

                            var mLocation: Location? = null

                            for (loc in locationResult.locations) {
                                mLocation = loc
                            }

                            if (mLocation != null) {
                                viewModel.location.postValue(mLocation)
                                //remove callback when location found, avoid infinite loops
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                            }
                        }
                    }

                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    )
                }
            }
    }

    private var navigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.nv_home -> {
                        replaceFragment(
                            HomeFragment.newInstance(""),
                            R.id.fl_content,
                            HomeFragment::class.simpleName
                        )
                        return true
                    }
//                    R.id.nav_dortodoor -> {
//                        replaceFragment(
//                            DoortoDoorFragment.newInstance(typeNotif.toString(), ""),
//                            R.id.fl_content,
//                            DoortoDoorFragment::class.simpleName
//                        )
//                        return true
//                    }
                    R.id.nv_order_history -> {
                        replaceFragment(
                            OrderHistoryFragment.newInstance(),
                            R.id.fl_content,
                            OrderHistoryFragment::class.simpleName
                        )
                        return true
                    }
                    R.id.nv_cart -> {
                        replaceFragment(
                            CartFragment.newInstance(),
                            R.id.fl_content,
                            CartFragment::class.simpleName
                        )
                        return true
                    }
                    R.id.nv_profile -> {
                        replaceFragment(
                            ProfileFragment.newInstance(),
                            R.id.fl_content,
                            ProfileFragment::class.simpleName
                        )
                        return true
                    }
                }
                return false
            }
        }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999

        fun getIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }
}
