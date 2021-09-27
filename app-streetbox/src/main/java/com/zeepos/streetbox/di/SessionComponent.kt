package com.zeepos.streetbox.di

import com.zeepos.streetbox.App
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

/**
 * Created by Arif S. on 5/2/20
 */

@SessionScope
@Subcomponent(
    modules = [
        InteractorsModule::class,
        APIsModule::class,
        ActivitiesModule::class,
        AndroidSupportInjectionModule::class,
        ViewModelFactoryModule::class
    ]
)
interface SessionComponent : AndroidInjector<App> {
    @Subcomponent.Builder
    abstract class Builder {
        abstract fun build(): SessionComponent
    }
}