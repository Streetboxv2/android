package id.streetbox.live.di

import com.zeepos.map.ui.MapUiEvent
import com.zeepos.payment.PaymentUiEvent
import id.streetbox.live.uiAPIs.LoginUiEventImpl
import id.streetbox.live.uiAPIs.MapUiEventImpl
import id.streetbox.live.uiAPIs.PaymentUiEventImpl
import id.streetbox.live.uiAPIs.SplashScreenUiEventImpl
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

    //    @ActivityScope
    @Binds
    internal abstract fun providePaymentUiEvents(
        paymentUiEventImpl: PaymentUiEventImpl
    ): PaymentUiEvent

}