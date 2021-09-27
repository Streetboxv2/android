package id.streetbox.live.ui.main.doortodoor

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class DoortoDoorModule {
    @Binds
    @IntoMap
    @ViewModelKey(DoortoDoorViewModel::class)
    abstract fun bindDoortoDoorViewModel(viewModel: DoortoDoorViewModel): ViewModel
}