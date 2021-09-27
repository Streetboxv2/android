package com.streetbox.pos.ui.main.order

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 2019-11-02
 */
@Module
abstract class OrderFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(OrderViewModel::class)
    abstract fun bindOrderViewModel(viewModel: OrderViewModel): ViewModel
}