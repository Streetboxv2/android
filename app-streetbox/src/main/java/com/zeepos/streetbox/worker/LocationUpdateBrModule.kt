package com.zeepos.streetbox.worker

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Arif S. on 8/8/20
 */
@Module
abstract class LocationUpdateBrModule {
    @ContributesAndroidInjector
    protected abstract fun locationBr(): LocationUpdatesBroadcastReceiver
}