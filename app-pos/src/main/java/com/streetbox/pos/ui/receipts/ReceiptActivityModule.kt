package com.streetbox.pos.ui.receipts

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 10/7/20
 */
@Module
abstract class ReceiptActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(ReceiptViewModel::class)
    abstract fun bindReceiptViewModel(viewModel: ReceiptViewModel): ViewModel
}