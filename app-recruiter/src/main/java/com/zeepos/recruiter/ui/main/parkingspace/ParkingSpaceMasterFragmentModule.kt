package com.zeepos.recruiter.ui.main.parkingspace

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ParkingSpaceMasterFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(ParkingSpaceMasterViewModel::class)
    abstract fun bindOrderViewModel(viewModel: ParkingSpaceMasterViewModel): ViewModel
}