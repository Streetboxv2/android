package com.zeepos.streetbox.ui.main.logs

import com.zeepos.domain.interactor.logs.LogsUseCase
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 7/1/20
 */
class LogViewModel @Inject constructor(
    private val logsUseCase: LogsUseCase
) : BaseViewModel<LogViewEvent>() {

    fun getLogs(isLoadMore: Boolean = false) {
        val disposable = logsUseCase.execute(LogsUseCase.Params(isLoadMore))
            .subscribe({
                if (it.isNotEmpty()) {
                    viewEventObservable.postValue(LogViewEvent.GetLogSuccess(it))
                }
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }
}