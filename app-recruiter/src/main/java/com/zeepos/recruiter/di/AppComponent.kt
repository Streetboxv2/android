package com.zeepos.recruiter.di

import com.zeepos.localstorage.LocalRepositoryModule
import com.zeepos.recruiter.App
import com.zeepos.remotestorage.RemoteRepositoryModule
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

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {
        abstract override fun build(): AppComponent
    }
}