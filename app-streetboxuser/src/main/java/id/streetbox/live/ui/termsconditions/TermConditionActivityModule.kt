package id.streetbox.live.ui.termsconditions
import id.streetbox.live.ui.menu.MenuViewModel

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/20/20
 */
@Module
abstract class TermConditionActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(TermConditionViewModel::class)
    abstract fun bindMenuViewModel(viewModel: TermConditionViewModel): ViewModel
}