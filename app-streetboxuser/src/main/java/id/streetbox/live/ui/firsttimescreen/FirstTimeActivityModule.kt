package id.streetbox.live.ui.firsttimescreen

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 8/4/20
 */
@Module
abstract class FirstTimeActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(FirstTimeViewModel::class)
    abstract fun bindMainViewModel(viewModel: FirstTimeViewModel): ViewModel
}