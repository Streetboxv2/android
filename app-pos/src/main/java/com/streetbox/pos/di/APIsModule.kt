package com.streetbox.pos.di

import com.streetbox.pos.uiAPIs.PaymentUiEventImpl
import com.zeepos.payment.PaymentUiEvent
import dagger.Binds
import dagger.Module

/**
 * Created by Arif S. on 5/2/20
 */
@Module
abstract class APIsModule {
    @Binds
    internal abstract fun providePaymentUiEvents(
        paymentUiEventImpl: PaymentUiEventImpl
    ): PaymentUiEvent
}