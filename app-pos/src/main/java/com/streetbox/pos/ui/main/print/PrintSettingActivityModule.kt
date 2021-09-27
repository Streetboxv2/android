package com.streetbox.pos.ui.main.print

import androidx.lifecycle.ViewModel
import com.streetbox.pos.ui.main.MainViewModel
import com.streetbox.pos.ui.main.product.ProductViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PrintSettingActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(PrintSettingViewModel::class)
    abstract fun bindMainViewModel(viewModel: PrintSettingViewModel): ViewModel
}