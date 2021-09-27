package com.zeepos.streetbox.di

import com.zeepos.map.ui.MapUiEvent
import com.zeepos.streetbox.uiAPIs.LoginUiEventImpl
import com.zeepos.streetbox.uiAPIs.MapUiEventImpl
import com.zeepos.streetbox.uiAPIs.SplashScreenUiEventImpl
import com.zeepos.ui_login.LoginUiEvent
import com.zeepos.ui_splashscreen.SplashScreenUiEvent
import dagger.Binds
import dagger.Module

/**
 * Created by Arif S. on 5/2/20
 */
@Module
abstract class APIsModule {

    @Binds
    internal abstract fun provideSplashScreenUiEvents(
        splashScreenUiEventImpl: SplashScreenUiEventImpl
    ): SplashScreenUiEvent

    //    @ActivityScope
    @Binds
    internal abstract fun provideLoginUiEvents(
        loginUiEvent: LoginUiEventImpl
    ): LoginUiEvent

    @Binds
    internal abstract fun provideMapUiEvents(
        mapUiEventImpl: MapUiEventImpl
    ): MapUiEvent

}