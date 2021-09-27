package com.zeepos.streetbox.ui.operator.operatortask

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class OperatorTaskModule {
    @Binds
    @IntoMap
    @ViewModelKey(OperatorTaskViewModel::class)
    abstract fun bindMyParkingSpaceViewModel(viewModel: OperatorTaskViewModel): ViewModel
}