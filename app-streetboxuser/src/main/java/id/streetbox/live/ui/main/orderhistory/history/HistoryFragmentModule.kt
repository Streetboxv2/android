package id.streetbox.live.ui.main.orderhistory.history

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import id.streetbox.live.ui.main.orderhistory.OrderHistoryViewModel

@Module
abstract class HistoryFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModel(viewModel: HistoryViewModel): ViewModel
}