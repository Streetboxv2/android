package id.streetbox.live.ui.termsconditions

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import com.zeepos.ui_splashscreen.SplashScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TermConditionModule {
    @Binds
    @IntoMap
    @ViewModelKey(TermConditionViewModel::class)
    abstract fun bindTermConditionViewModel(viewModel: TermConditionViewModel): ViewModel
}