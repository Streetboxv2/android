package com.zeepos.streetbox.di

import dagger.Module
import dagger.Provides

/**
 * Created by Arif S. on 5/2/20
 */

@Module(subcomponents = [SessionComponent::class])
class SessionProviderModule {
    @Provides
    fun sessionComponentBuilder(builder: SessionComponent.Builder): SessionComponent {
        return builder.build()
    }
}