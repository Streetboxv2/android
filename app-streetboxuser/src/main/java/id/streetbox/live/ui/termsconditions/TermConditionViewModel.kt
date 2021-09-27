package id.streetbox.live.ui.termsconditions

import android.content.Context
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Arif S. on 6/12/20
 */
class TermConditionViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : BaseViewModel<TermConditionViewEvent>() {

    fun callApiTermCondition() {
        val disposable = remoteRepository.getDataTermCondition()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(TermConditionViewEvent.OnSuccessTermCondition(it))
            }, {
                viewEventObservable.postValue(
                    TermConditionViewEvent.OnFailedTermCondition(it)
                )
            })
        addDisposable(disposable)

    }
}