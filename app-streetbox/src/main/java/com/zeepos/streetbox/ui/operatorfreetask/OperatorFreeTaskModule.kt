package com.zeepos.streetbox.ui.operatorfreetask

import androidx.lifecycle.ViewModel
import com.zeepos.streetbox.ui.operator.OperatorHomeViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class OperatorFreeTaskModule {
    @Binds
    @IntoMap
    @ViewModelKey(OperatorFreeTaskViewModel::class)
    abstract fun bindOperatorViewModel(viewModel: OperatorFreeTaskViewModel): ViewModel

}