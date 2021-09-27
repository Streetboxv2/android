package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import io.reactivex.Completable
import java.io.File
import javax.inject.Inject

/**
 * Created by Arif S. on 8/13/20
 */
class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : CompletableUseCase<UpdateUserUseCase.Params>() {
    data class Params(val user: User, val file: File?)

    override fun buildUseCaseCompletable(params: Params): Completable {
        return userRepository.updateUser(params.user, params.file)
    }

}