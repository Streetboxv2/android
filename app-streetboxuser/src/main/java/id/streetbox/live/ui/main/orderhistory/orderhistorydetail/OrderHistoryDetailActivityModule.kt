package id.streetbox.live.ui.main.orderhistory.orderhistorydetail

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 8/10/20
 */
@Module
abstract class OrderHistoryDetailActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(OrderHistoryDetailViewModel::class)
    abstract fun bindOrderHistoryViewModel(viewModel: OrderHistoryDetailViewModel): ViewModel
}