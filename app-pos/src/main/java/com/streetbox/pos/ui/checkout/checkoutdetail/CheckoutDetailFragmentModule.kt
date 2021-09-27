package com.streetbox.pos.ui.checkout.checkoutdetail

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 7/12/20
 */
@Module
abstract class CheckoutDetailFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(CheckoutDetailViewModel::class)
    abstract fun bindCheckoutViewModel(viewModel: CheckoutDetailViewModel): ViewModel
}