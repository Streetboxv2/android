package id.streetbox.live.di

import com.zeepos.localstorage.LocalRepositoryModule
import com.zeepos.remotestorage.RemoteRepositoryModule
import id.streetbox.live.App
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
        SessionModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    fun androidInjector(): AndroidInjector<App>

    fun workerComponentBuilder(): WorkerComponent.Builder

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {
        abstract override fun build(): AppComponent
    }
}