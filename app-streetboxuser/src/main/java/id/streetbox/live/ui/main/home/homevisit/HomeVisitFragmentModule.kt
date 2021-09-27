package id.streetbox.live.ui.main.home.homevisit

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/14/20
 */
@Module
abstract class HomeVisitFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeVisitViewModel::class)
    abstract fun bindHomeVisitViewModel(viewModel: HomeVisitViewModel): ViewModel
}