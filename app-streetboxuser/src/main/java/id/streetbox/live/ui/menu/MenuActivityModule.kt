package id.streetbox.live.ui.menu

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/20/20
 */
@Module
abstract class MenuActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(MenuViewModel::class)
    abstract fun bindMenuViewModel(viewModel: MenuViewModel): ViewModel
}