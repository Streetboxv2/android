package com.streetbox.pos

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.mazenrashed.printooth.Printooth
import com.streetbox.pos.di.AppComponent
import com.streetbox.pos.di.DaggerAppComponent
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.views.TypefaceHelper
import com.zeepos.utilities.SharedPreferenceUtil
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

/**
 * Created by Arif S. on 7/2/20
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
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        TypefaceHelper.init(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        SharedPreferenceUtil.setString(this, ConstVar.APP_TYPE, ConstVar.APP_POS)
        Printooth.init(this)

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val builder: AppComponent.Builder = DaggerAppComponent.builder()
        builder.seedInstance(this)
        val appComponent = builder.build()

        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(
                appComponent.workerComponentBuilder().build().daggerWorkerFactory()
            ).build()
        )

        return appComponent.androidInjector()
    }
}