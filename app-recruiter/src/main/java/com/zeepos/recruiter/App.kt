package com.zeepos.recruiter

import android.content.Context
import androidx.multidex.MultiDex
import com.zeepos.models.ConstVar
import com.zeepos.recruiter.di.AppComponent
import com.zeepos.recruiter.di.DaggerAppComponent
import com.zeepos.utilities.SharedPreferenceUtil
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

/**
 * Created by Arif S. on 5/7/20
 */
class App : DaggerApplication() {
    companion object {
        private lateinit var application: App

        fun getInstance(): App {
            return application
        }
    }

    init {
        application = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        SharedPreferenceUtil.setString(this, ConstVar.APP_TYPE, ConstVar.APP_RECRUITER)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val builder: AppComponent.Builder = DaggerAppComponent.builder()
        builder.seedInstance(this)
        return builder.build().androidInjector()
    }
}