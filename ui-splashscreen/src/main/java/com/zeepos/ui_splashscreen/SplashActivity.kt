package com.zeepos.ui_splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.SharedPreferenceUtil
import com.zeepos.utilities.intentPageData
import io.objectbox.BoxStore
import kotlinx.android.synthetic.main.splashscreen.*
import javax.inject.Inject

class SplashActivity : BaseActivity<SplashScreenViewEvent, SplashScreenViewModel>() {

    @Inject
    lateinit var splashScreenUiEvent: SplashScreenUiEvent
    private var isFirstTime = false
    var typeNotif: String? = null

    private val appType: String by lazy {
        SharedPreferenceUtil.getString(this, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
            ?: ConstVar.EMPTY_STRING
    }

    private val shift: Boolean by lazy {
        SharedPreferenceUtil.getBoolean(this, ConstVar.SHIFT, false)
    }

    override fun initResourceLayout(): Int {
        return R.layout.splashscreen
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(SplashScreenViewModel::class.java)
//        checkNotifBundle()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        when (appType) {
            ConstVar.APP_MERCHANT -> {
                rl_background.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                logo.setImageResource(R.drawable.logo_with_text)
            }
            ConstVar.APP_CUSTOMER -> {
                isFirstTime = SharedPreferenceUtil.getBoolean(this, ConstVar.FIRSTTIME, true)
                rl_background.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
                logo.setImageResource(R.drawable.logo_end_user_with_text)
            }
            else -> {
                rl_background.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                logo.setImageResource(R.drawable.logo_with_text)
            }
        }

        Handler().postDelayed({
            if (isFirstTime) {
                SharedPreferenceUtil.setBoolean(this, ConstVar.FIRSTTIME, false)
                splashScreenUiEvent.goToFirstTimeAppScreen(this)
            } else {
                viewModel.checkUserSession()
            }
        }, 2000)
    }

    private fun checkNotifBundle() {
        if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
                val value = intent.extras!![key]
                println("respon Activity onResume Key: $key Value: $value")
                splashScreenUiEvent.goToMainActivity(this, "listnotif")
            }
        }
    }


    fun checkStatusShift() {
        if (shift == true) {
            splashScreenUiEvent.goToOperatorHomeScreen(this)
        } else {
            splashScreenUiEvent.goToOperatorMainScreen(this)
        }
    }

    override fun onEvent(useCase: SplashScreenViewEvent) {
        when (useCase) {
            SplashScreenViewEvent.GoToMainScreen -> {
                if (intent.extras != null) {
                    for (key in intent.extras!!.keySet()) {
                        val value = intent.extras!![key]
                        println("respon Activity onResume Key: $key Value: $value")
                    }
                    splashScreenUiEvent.goToMainActivity(this, "listnotif")
                } else splashScreenUiEvent.goToMainScreen(this)
            }
            SplashScreenViewEvent.GoToOperatorMainScreen ->
                checkStatusShift()
            SplashScreenViewEvent.GoToLoginScreen -> splashScreenUiEvent.goToLoginScreen(this)
        }
    }
}