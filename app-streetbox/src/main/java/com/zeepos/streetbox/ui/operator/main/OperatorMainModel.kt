package com.zeepos.streetbox.ui.operator.main

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class OperatorMainModel @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository
) : BaseViewModel<OperatorMainViewEvent>() {

    val location: MutableLiveData<Location> = MutableLiveData()

    fun logout() {
        localPreferencesRepository.deleteSession()
    }

}