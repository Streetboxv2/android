package com.streetbox.pos.ui.main.onlineorder.orderbill

import androidx.lifecycle.ViewModel
import com.streetbox.pos.ui.main.onlineorder.orderbil.OrderBillViewModel
import com.streetbox.pos.ui.main.order.OrderViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class OrderBillFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(OrderBillViewModel::class)
    abstract fun bindOrderViewModel(viewModel: OrderBillViewModel): ViewModel
}