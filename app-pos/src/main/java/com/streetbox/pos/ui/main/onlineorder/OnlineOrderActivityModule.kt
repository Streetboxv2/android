package com.streetbox.pos.ui.main.onlineorder

import androidx.lifecycle.ViewModel
import com.streetbox.pos.ui.main.order.OrderViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class OnlineOrderActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(OnlineOrderViewModel::class)
    abstract fun bindOrderViewModel(viewModel: OnlineOrderViewModel): ViewModel
}