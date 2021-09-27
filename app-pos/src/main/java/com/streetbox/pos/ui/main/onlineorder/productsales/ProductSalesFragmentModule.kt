package com.streetbox.pos.ui.main.onlineorder.paymentsales

import androidx.lifecycle.ViewModel
import com.streetbox.pos.ui.main.onlineorder.productsales.ProductSalesViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProductSalesFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProductSalesViewModel::class)
    abstract fun bindItemViewModel(viewModel: ProductSalesViewModel): ViewModel
}