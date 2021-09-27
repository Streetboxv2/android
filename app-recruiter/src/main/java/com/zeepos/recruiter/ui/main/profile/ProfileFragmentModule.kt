package com.zeepos.recruiter.ui.main.profile

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProfileFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindOrderViewModel(viewModel: ProfileViewModel): ViewModel
}