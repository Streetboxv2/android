package com.zeepos.streetbox.ui.main.parkingspace

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 5/13/20
 */
@Module
abstract class ParkingSpaceFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(ParkingSpaceViewModel::class)
    abstract fun bindOrderViewModel(viewModel: ParkingSpaceViewModel): ViewModel
}