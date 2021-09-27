package com.zeepos.localstorage

import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.master.UserAuthData
import io.objectbox.BoxStore
import javax.inject.Inject

/**
 * Created by Arif S. on 5/12/20
 */
class LocalPreferenceRepositoryImpl @Inject internal constructor(
    private val storage: Storage,
    private val userRepository: UserRepository
) : LocalPreferencesRepository {
    override fun checkSessionUser(): UserAuthData? {
        return storage[ConstVar.USER_AUTH, UserAuthData::class.java]
    }

    override fun getCurrentUserToken(): String? {
        return storage[ConstVar.USER_AUTH, UserAuthData::class.java]?.accessToken
    }

    override fun deleteSession() {
        storage.clearAll()
        userRepository.deleteAllObs()
    }
}