package com.zeepos.map.ui.merchantschedule

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 8/12/20
 */
@Module
abstract class MerchantScheduleFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(MerchantScheduleViewModel::class)
    abstract fun bindMerchantScheduleViewModel(viewModel: MerchantScheduleViewModel): ViewModel
}