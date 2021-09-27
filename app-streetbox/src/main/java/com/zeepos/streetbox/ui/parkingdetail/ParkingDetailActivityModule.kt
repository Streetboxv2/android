package com.zeepos.streetbox.ui.parkingdetail

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 5/15/20
 */
@Module
abstract class ParkingDetailActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(ParkingDetailViewModel::class)
    abstract fun bindParkingDetailViewModel(viewModel: ParkingDetailViewModel): ViewModel
}