package id.streetbox.live.ui.main.home.nearby

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/14/20
 */
@Module
abstract class NearByFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(NearByViewModel::class)
    abstract fun bindNearByViewModel(viewModel: NearByViewModel): ViewModel
}