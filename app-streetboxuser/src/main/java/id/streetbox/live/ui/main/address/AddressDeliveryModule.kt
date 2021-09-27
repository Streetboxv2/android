package id.streetbox.live.ui.main.address

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import id.streetbox.live.ui.main.home.nearby.NearByViewModel

@Module
abstract class AddressDeliveryModule {
    @Binds
    @IntoMap
    @ViewModelKey(AddressViewModel::class)
    abstract fun bindAddressViewModel(viewModel: AddressViewModel): ViewModel
}