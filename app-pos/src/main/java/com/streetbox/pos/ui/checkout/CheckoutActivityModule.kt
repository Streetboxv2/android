package com.streetbox.pos.ui.checkout

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 7/12/20
 */
@Module
abstract class CheckoutActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(CheckoutViewModel::class)
    abstract fun bindCheckoutViewModel(viewModel: CheckoutViewModel): ViewModel
}