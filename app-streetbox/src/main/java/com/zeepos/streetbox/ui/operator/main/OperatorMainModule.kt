package com.zeepos.streetbox.ui.operator.main

import androidx.lifecycle.ViewModel
import com.zeepos.streetbox.ui.main.MainViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class OperatorMainModule {
    @Binds
    @IntoMap
    @ViewModelKey(OperatorMainModel::class)
    abstract fun bindOperatorMainModel(viewModel: OperatorMainModel): ViewModel
}