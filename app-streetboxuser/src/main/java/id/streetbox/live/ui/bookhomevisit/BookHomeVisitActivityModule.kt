package id.streetbox.live.ui.bookhomevisit

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/25/20
 */
@Module
abstract class BookHomeVisitActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(BookHomeVisitViewModel::class)
    abstract fun bindBookHomeVisitViewModel(viewModel: BookHomeVisitViewModel): ViewModel
}