package com.zeepos.streetbox.ui.main

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.main.logs.LogFragment
import com.zeepos.streetbox.ui.main.myparkingspace.MyParkingSpaceFragment
import com.zeepos.streetbox.ui.main.parkingspace.ParkingSpaceFragment
import com.zeepos.streetbox.ui.main.profile.ProfileFragment
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.SharedPreferenceUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity<MainViewEvent, MainViewModel>() {

    private var doubleBackToExitPressedOnce = false
    var typeNotif: String? = null

    override fun initResourceLayout(): Int {
        return R.layout.activity_main
    }

    override fun init() {
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

                viewModel.sendToken(token!!)
            })

//        clearNotification()

        viewModel = ViewModelProvider(this, viewModeFactory).get(MainViewModel::class.java)


        SharedPreferenceUtil.setString(
            applicationContext,
            ConstVar.TOKEN,
            viewModel.getCurrentUserToken()
        )

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        typeNotif = intent.getStringExtra("typeNotif")
    }


    fun clearNotification() {
        val notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        if(typeNotif!=null){
            addFragment(MyParkingSpaceFragment.newInstance(), R.id.fl_content)
        }else{
            addFragment(ParkingSpaceFragment.newInstance(), R.id.fl_content)
        }

        nav_view.setOnNavigationItemSelectedListener(navigationItemSelectedListener)

    }

    private var navigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navigation_parkingspace -> {
                        replaceFragment(
                            ParkingSpaceFragment.newInstance(),
                            R.id.fl_content,
                            ParkingSpaceFragment::class.simpleName
                        )
                        return true
                    }
                    R.id.navigation_my_parking_space -> {
                        replaceFragment(
                            MyParkingSpaceFragment.newInstance(),
                            R.id.fl_content,
                            MyParkingSpaceFragment::class.simpleName
                        )
                        return true
                    }
                    R.id.navigation_log_activity -> {
                        replaceFragment(
                            LogFragment.newInstance(),
                            R.id.fl_content,
                            LogFragment::class.simpleName
                        )
                        return true
                    }
                    R.id.navigation_profile -> {
                        replaceFragment(
                            ProfileFragment.newInstance(),
                            R.id.fl_content,
                            ProfileFragment::class.simpleName
                        )
                        return true
                    }
//                    R.id.navigation_cart -> {
//                        startActivity(CartActivity.getIntent(this@MainActivity))
//                        return true
//                    }
                }
                return false
            }
        }

    override fun onEvent(useCase: MainViewEvent) {
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
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

}
