package com.zeepos.streetbox.di

import androidx.lifecycle.ViewModelProvider
import com.zeepos.ui_base.ui.DaggerViewModelFactory
import dagger.Binds
import dagger.Module

/**
 * Created by Arif S. on 5/2/20
 */

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory
}