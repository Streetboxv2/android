package com.streetbox.pos.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import com.streetbox.pos.App
import com.streetbox.pos.worker.ChildWorkerFactory
import com.streetbox.pos.worker.DaggerWorkerFactory
import com.streetbox.pos.worker.SyncTransactionScheduledWorker
import com.streetbox.pos.worker.SyncTransactionWorker
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
    @WorkerKey(SyncTransactionScheduledWorker::class)
    abstract fun bindTransactionWorker(factory: SyncTransactionScheduledWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(SyncTransactionWorker::class)
    abstract fun bindSyncTransactionWorker(factory: SyncTransactionWorker.Factory): ChildWorkerFactory
}