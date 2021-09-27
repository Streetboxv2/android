package id.streetbox.live

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.zeepos.map.utils.MapUtils
import com.zeepos.models.ConstVar
import com.zeepos.utilities.SharedPreferenceUtil
import com.zeepos.utilities.initHawk
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import com.example.dbroom.db.room.AppDatabase
import id.streetbox.live.di.AppComponent
import id.streetbox.live.di.DaggerAppComponent

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
        FirebaseApp.initializeApp(this)
        MapUtils.init(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        SharedPreferenceUtil.setString(this, ConstVar.APP_TYPE, ConstVar.APP_CUSTOMER)
        initHawk(this)
        createDataBaseRoom()
    }

    fun createDataBaseRoom() {
        AppDatabase.getInstance(this)
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