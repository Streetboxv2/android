package com.zeepos.map.ui.merchantinstagram

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 8/12/20
 */
@Module
abstract class MerchantInstagramFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(MerchantInstagramViewModel::class)
    abstract fun bindMerchantInstagramViewModel(viewModel: MerchantInstagramViewModel): ViewModel
}