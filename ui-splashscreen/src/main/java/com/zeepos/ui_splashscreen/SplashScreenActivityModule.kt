package com.zeepos.ui_splashscreen

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/15/20
 */
@Module
abstract class SplashScreenActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashScreenViewModel::class)
    abstract fun bindSplashScreenViewModel(viewModel: SplashScreenViewModel): ViewModel
}