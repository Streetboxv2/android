package com.zeepos.remotestorage

import com.zeepos.domain.repository.RemoteRepository
import dagger.Binds
import dagger.Module

/**
 * Created by Arif S. on 5/2/20
 */
@Module
abstract class RemoteRepositoryModule {
    @Binds
    protected abstract fun remoteRepository(remoteRepository: RemoteRepositoryImpl): RemoteRepository
}