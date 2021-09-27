package com.zeepos.streetbox.ui.operator

import androidx.lifecycle.ViewModel
import com.zeepos.streetbox.ui.operator.main.OperatorMainModel
import com.zeepos.streetbox.ui.operator.main.OperatorMainModule
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class OperatorHomeModule {
    @Binds
    @IntoMap
    @ViewModelKey(OperatorHomeViewModel::class)
    abstract fun bindOperatorViewModel(viewModel: OperatorHomeViewModel): ViewModel

}