package id.streetbox.live.ui.orderreview.homevisit

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 8/10/20
 */
@Module
abstract class BookHomeVisitOrderActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(BookHomeVisitOrderViewModel::class)
    abstract fun bindBookHomeVisitOrderViewModel(viewModel: BookHomeVisitOrderViewModel): ViewModel

}