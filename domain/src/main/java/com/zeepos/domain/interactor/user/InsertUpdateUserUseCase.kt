package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 5/12/20
 */
class InsertUpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : CompletableUseCase<User>() {
    override fun buildUseCaseCompletable(params: User): Completable {
        return Completable.fromAction { userRepository.insertUpdateUser(params) }
    }
}