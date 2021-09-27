package com.zeepos.ui_password

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import com.zeepos.ui_login.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ForgotPasswordModule {
    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    abstract fun bindMainViewModel(viewModel: ForgotPasswordViewModel): ViewModel
}