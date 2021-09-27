package id.streetbox.live.ui.main.home

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/13/20
 */
@Module
abstract class HomeFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

}