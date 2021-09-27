package com.streetbox.pos.ui.setting

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 7/20/20
 */
@Module
abstract class SettingActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindSettingViewModel(viewModel: SettingViewModel): ViewModel
}