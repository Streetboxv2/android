package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.SynchronousUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.models.entities.None
import com.zeepos.models.master.UserAuthData
import javax.inject.Inject

/**
 * Created by Arif S. on 5/12/20
 */
class CheckSessionUserUseCase @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository
) : SynchronousUseCase<UserAuthData?, None> {
    override fun execute(params: None): UserAuthData? {
        return localPreferencesRepository.checkSessionUser()
    }

}