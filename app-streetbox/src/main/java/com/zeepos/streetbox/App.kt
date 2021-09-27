package com.zeepos.streetbox

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.zeepos.map.utils.MapUtils
import com.zeepos.models.ConstVar
import com.zeepos.streetbox.di.AppComponent
import com.zeepos.streetbox.di.DaggerAppComponent
import com.zeepos.ui_base.views.TypefaceHelper
import com.zeepos.utilities.SharedPreferenceUtil
import com.zeepos.utilities.initHawk
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.objectbox.BoxStore

/**
 * Created by Arif S. on 5/2/20
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
//        BoxStore.deleteAllFiles(this, null)
        FirebaseApp.initializeApp(this)
        TypefaceHelper.init(this)
        MapUtils.init(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        SharedPreferenceUtil.setString(this, ConstVar.APP_TYPE, ConstVar.APP_MERCHANT)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.d("respon FMS_TOKEN food", it.token)
        }

        initHawk(this)

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