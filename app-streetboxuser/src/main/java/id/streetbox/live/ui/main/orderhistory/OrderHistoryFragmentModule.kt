package id.streetbox.live.ui.main.orderhistory

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/13/20
 */
@Module
abstract class OrderHistoryFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(OrderHistoryViewModel::class)
    abstract fun bindOrderHistoryViewModel(viewModel: OrderHistoryViewModel): ViewModel
}