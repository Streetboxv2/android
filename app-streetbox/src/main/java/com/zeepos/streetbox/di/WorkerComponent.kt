package com.zeepos.streetbox.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import com.zeepos.streetbox.App
import com.zeepos.streetbox.worker.ChildWorkerFactory
import com.zeepos.streetbox.worker.DaggerWorkerFactory
import com.zeepos.streetbox.worker.LocationUpdateWorker
import com.zeepos.ui_base.di.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 7/9/20
 */
@Subcomponent(
    modules = [
        AndroidSupportInjectionModule::class,
        WorkerAssistedInjectModule::class,
        WorkerBindModule::class
    ]
)
interface WorkerComponent : AndroidInjector<App> {

    fun daggerWorkerFactory(): DaggerWorkerFactory

//    fun workers(): Map<Class<out RxWorker>, Provider<RxWorker>>

    @Subcomponent.Builder
    abstract class Builder {
        abstract fun build(): WorkerComponent
    }
}

@Module(includes = [AssistedInject_WorkerAssistedInjectModule::class])
@AssistedModule
interface WorkerAssistedInjectModule

@Module
abstract class WorkerBindModule {
    @Binds
    @IntoMap
    @WorkerKey(LocationUpdateWorker::class)
    abstract fun bindTransactionWorker(factory: LocationUpdateWorker.Factory): ChildWorkerFactory
}