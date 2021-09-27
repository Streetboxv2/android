package com.streetbox.pos.di

import com.streetbox.pos.App
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector

/**
 * Created by Arif S. on 5/2/20
 */

@SuppressWarnings("StaticMethodOnlyUsedInOneClass", "squid:S1118", "squid:S1610")
@Module(includes = [SessionProviderModule::class])
internal abstract class SessionModule {
    @Binds
    internal abstract fun injector(component: SessionComponent): AndroidInjector<App>
}