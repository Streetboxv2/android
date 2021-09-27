package id.streetbox.live.ui.pickuporder

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import id.streetbox.live.ui.orderreview.pickup.PickUpOrderReviewViewModel

@Module
abstract class PickupOrderActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(PickUpOrderReviewViewModel::class)
    abstract fun bindBookHomeVisitOrderViewModel(viewModel: PickUpOrderReviewViewModel): ViewModel
}