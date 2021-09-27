package com.zeepos.streetbox.di

import com.zeepos.localstorage.LocalRepositoryModule
import com.zeepos.map.workers.LocationUpdateWorker
import com.zeepos.remotestorage.RemoteRepositoryModule
import com.zeepos.streetbox.App
import com.zeepos.streetbox.worker.LocationUpdateBrModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by Arif S. on 5/2/20
 */

@Singleton
@Component(

    modules = [
        DomainToolsModule::class,
        LocalRepositoryModule::class,
        RemoteRepositoryModule::class,
        AndroidSupportInjectionModule::class,
        SessionModule::class,
        LocationUpdateBrModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    fun androidInjector(): AndroidInjector<App>

    fun workerComponentBuilder(): WorkerComponent.Builder

    fun inject(worker: LocationUpdateWorker)

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {
        abstract override fun build(): AppComponent
    }
}