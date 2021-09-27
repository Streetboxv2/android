package com.zeepos.streetbox.ui.broadcast

import androidx.lifecycle.ViewModel
import com.zeepos.streetbox.ui.cart.CartViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class BroadCastModule {
    @Binds
    @IntoMap
    @ViewModelKey(BroadCastViewModel::class)
    abstract fun bindBroadcastViewModel(viewModel: BroadCastViewModel): ViewModel
}