package com.streetbox.pos.ui.main

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.streetbox.pos.R
import com.streetbox.pos.ui.main.order.OrderFragment
import com.streetbox.pos.ui.main.product.ProductFragment
import com.streetbox.pos.ui.notification.MessageEvent
import com.streetbox.pos.ui.receipts.ReceiptViewEvent
import com.streetbox.pos.worker.SyncTransactionScheduledWorker
import com.streetbox.pos.worker.SyncTransactionWorker
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.SharedPreferenceUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity<MainViewEvent, MainViewModel>() {

    private var doubleBackToExitPressedOnce = false
    private var startDate: Long = DateTimeUtil.getCurrentLocalDateWithoutTime()
    private var endDate: Long = DateTimeUtil.getCurrentLocalDateWithoutTime()

    override fun initResourceLayout(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(MainViewModel::class.java)
//        SyncTransactionWorker.syncTransactionData(this,"")
//        FirebaseMessaging.getInstance().subscribeToTopic("onlineOrder")

//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w("TAG TOKEN", "getInstanceId failed", task.exception)
//                    return@OnCompleteListener
//                }
//                // Get new Instance ID token
//                val token = task.result?.token
//                // Log and toast
//
//                Log.d("TAG", "$token")
//
//                if (token != null)
//                    viewModel.sendToken(token)
//            })
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
//            val msg = getString(R.string.project_id, token)
//            Log.d(("TAG", msg)
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "$token")

                if (token != null)
                    viewModel.sendToken(token)
        })
        var token = FirebaseMessaging.getInstance().token
        if (token != null)
            viewModel.sendToken(token.result)
//        val user = viewModel.getUserLocal()
//        user?.let {
//            FirebaseMessaging.getInstance().subscribeToTopic("blast_${user?.id}")
//                .addOnSuccessListener {
//                }.addOnFailureListener {
//                }
//        }

//        clearNotification()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) { /* Do something */

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        try {
            viewModel.getAllTransaction(startDate, endDate, "")
        } catch(e: Exception) {
            e.printStackTrace()
            addFragment(
                ProductFragment.newInstance(),
                R.id.fl_left,
                ProductFragment::class.java.simpleName
            )
            addFragment(
                OrderFragment.newInstance(),
                R.id.fl_right,
                OrderFragment::class.java.simpleName
            )
            viewModel.getRecentOrder()
        }
    }

    fun clearNotification() {
        val notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onEvent(useCase: MainViewEvent) {
        when (useCase) {
            MainViewEvent.OrderFailedCreated -> Toast.makeText(
                this,
                "Open or Create Order Failed",
                Toast.LENGTH_SHORT
            ).show()
            is MainViewEvent.OnAddItemFailed -> {
                Toast.makeText(this,"Out Of Stock",Toast.LENGTH_SHORT).show()
            }

        }
        when (useCase) {
            is MainViewEvent.GetAllTransactionSuccess -> {
                addFragment(
                    ProductFragment.newInstance(),
                    R.id.fl_left,
                    ProductFragment::class.java.simpleName
                )
                addFragment(
                    OrderFragment.newInstance(),
                    R.id.fl_right,
                    OrderFragment::class.java.simpleName
                )
                viewModel.getRecentOrder()
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
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

