package com.zeepos.domain.interactor.operator

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 6/18/20
 */
class SearchOperatorUseCase @Inject constructor(
    private val userRepository: UserRepository
) :
    SingleUseCase<List<User>, SearchOperatorUseCase.Params>() {
    data class Params(val keyword: String)

    override fun buildUseCaseSingle(params: Params): Single<List<User>> {
        return userRepository.searchOperator(params.keyword)
    }
}