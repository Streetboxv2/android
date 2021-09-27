package com.zeepos.streetbox.ui.operator.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.zeepos.map.utils.PermissionUtils
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.operator.OperatorHomeFragment
import com.zeepos.ui_base.ui.BaseActivity

class OperatorMainActivity : BaseActivity<OperatorMainViewEvent, OperatorMainModel>() {

    private var doubleBackToExitPressedOnce = false
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0
    private var type: Int = 0

    override fun initResourceLayout(): Int {
        return R.layout.operator_main
    }

    override fun init() {
        val data = intent.extras
        latitude = data!!.getDouble("lat")
        longitude = data.getDouble("lon")
        type = data.getInt("type")
        viewModel = ViewModelProvider(this, viewModeFactory).get(OperatorMainModel::class.java)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.optionmenu, menu)
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === R.id.about) {

        } else if (item.getItemId() === R.id.setting) {
            viewModel.logout()
        } else if (item.getItemId() === R.id.help) {

        }
        return true
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        addFragment(
            OperatorHomeFragment.newInstance(),
            R.id.fl_contentOperator,
            OperatorHomeFragment::class.simpleName,
            latitude!!,
            longitude!!,
            type
        )
    }

    override fun onEvent(useCase: OperatorMainViewEvent) {
    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
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
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
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

                    finish()
                }
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

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    companion object {

        private const val LOCATION_PERMISSION_REQUEST_CODE = 999

        fun getIntent(context: Context, latitude: Double, longitude: Double, type: Int): Intent {
            val intent = Intent(context, OperatorMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val bundle = Bundle()
            bundle.putDouble("lat", latitude)
            bundle.putDouble("lon", longitude)
            bundle.putInt("type", type)
            intent.putExtras(bundle)
            return intent
        }
    }
}
