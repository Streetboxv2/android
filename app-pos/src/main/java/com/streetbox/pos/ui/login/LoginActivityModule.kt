package com.streetbox.pos.ui.login

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 5/2/20
 */

@Module
abstract class LoginActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindMainViewModel(viewModel: LoginViewModel): ViewModel
}