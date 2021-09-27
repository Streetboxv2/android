package id.streetbox.live.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import id.streetbox.live.App
import id.streetbox.live.worker.ChildWorkerFactory
import id.streetbox.live.worker.DaggerWorkerFactory
import id.streetbox.live.worker.SyncHomeVisitTransactionWorker
import id.streetbox.live.worker.SyncTransactionWorker
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
    @WorkerKey(SyncTransactionWorker::class)
    abstract fun bindTransactionWorker(factory: SyncTransactionWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(SyncHomeVisitTransactionWorker::class)
    abstract fun bindHomeVisitTransactionWorker(factory: SyncHomeVisitTransactionWorker.Factory): ChildWorkerFactory
}