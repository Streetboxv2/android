package com.zeepos.streetbox.ui.cart

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 5/17/20
 */
@Module
abstract class CartActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(CartViewModel::class)
    abstract fun bindOrderViewModel(viewModel: CartViewModel): ViewModel
}