package com.zeepos.recruiter.ui.main

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class RecruitMainActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(RecruitMainViewModel::class)
    abstract fun bindMainViewModel(viewModel: RecruitMainViewModel): ViewModel
}