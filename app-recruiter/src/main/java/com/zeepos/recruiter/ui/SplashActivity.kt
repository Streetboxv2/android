package com.zeepos.recruiter.ui

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.zeepos.recruiter.R

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        Handler().postDelayed({
            finish()
        }, 2000)
    }
}