package com.zeepos.recruiter.ui.main.form

import androidx.lifecycle.ViewModel
import com.zeepos.recruiter.ui.main.RecruitMainViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FormParkingSpaceActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(FormParkingSpaceViewModel::class)
    abstract fun bindMainViewModel(viewModel: FormParkingSpaceViewModel): ViewModel
}