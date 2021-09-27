package com.zeepos.streetbox.ui.operator.main

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class OperatorFTModule {
    @Binds
    @IntoMap
    @ViewModelKey(OperatorFTViewModel::class)
    abstract fun bindOrderViewModel(viewModel: OperatorFTViewModel): ViewModel
}