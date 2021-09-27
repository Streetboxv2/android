package id.streetbox.live.ui.orderreview.pickup

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 7/24/20
 */
@Module
abstract class PickUpOrderReviewActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(PickUpOrderReviewViewModel::class)
    abstract fun bindPickUpOrderReviewViewModel(viewModel: PickUpOrderReviewViewModel): ViewModel
}