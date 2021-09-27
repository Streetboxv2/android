package com.zeepos.streetbox.ui.operatormerchant

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 5/16/20
 */
@Module
abstract class OperatorActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(OperatorViewModel::class)
    abstract fun bindOrderViewModel(viewModel: OperatorViewModel): ViewModel
}