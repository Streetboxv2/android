package com.zeepos.streetbox.ui.main.logs

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 7/1/20
 */
@Module
abstract class LogFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(LogViewModel::class)
    abstract fun bindLogViewModel(viewModel: LogViewModel): ViewModel
}