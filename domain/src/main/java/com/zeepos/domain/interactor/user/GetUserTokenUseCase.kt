package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.SynchronousUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.models.entities.None
import javax.inject.Inject

/**
 * Created by Arif S. on 5/20/20
 */
class GetUserTokenUseCase @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository
) : SynchronousUseCase<String?, None> {
    override fun execute(params: None): String? {
        return localPreferencesRepository.getCurrentUserToken()
    }
}