package com.zeepos.streetbox.ui.main.myparkingspace

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 5/21/20
 */
@Module
abstract class MyParkingSpaceFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(MyParkingSpaceViewModel::class)
    abstract fun bindMyParkingSpaceViewModel(viewModel: MyParkingSpaceViewModel): ViewModel
}