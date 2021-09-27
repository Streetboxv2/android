package com.zeepos.domain.repository

import com.zeepos.models.master.UserAuthData

/**
 * Created by Arif S. on 5/12/20
 */
interface LocalPreferencesRepository {
    fun checkSessionUser(): UserAuthData?
    fun getCurrentUserToken(): String?
    fun deleteSession()
}