package com.streetbox.pos.ui.main.product

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 2019-11-02
 */
@Module
abstract class ProductFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProductViewModel::class)
    abstract fun bindItemViewModel(viewModel: ProductViewModel): ViewModel
}