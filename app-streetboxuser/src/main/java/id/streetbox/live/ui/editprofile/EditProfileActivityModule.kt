package id.streetbox.live.ui.editprofile

import androidx.lifecycle.ViewModel
import com.zeepos.ui_base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Arif S. on 6/12/20
 */
@Module
abstract class EditProfileActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(EditProfileViewModel::class)
    abstract fun bindEditProfileViewModel(viewModel: EditProfileViewModel): ViewModel
}