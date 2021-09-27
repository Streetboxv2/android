package com.zeepos.payment

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 5/18/20
 */
@Module
abstract class PaymentActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    abstract fun bindPaymentViewModel(viewModel: PaymentViewModel): ViewModel
}