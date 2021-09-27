package com.zeepos.domain.repository

import com.zeepos.domain.interactor.user.LoginPosUseCase
import com.zeepos.models.master.User
import com.zeepos.models.master.UserAuthData
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.io.File

/**
 * Created by Arif S. on 5/7/20
 */
interface UserRepository {
    fun insertUpdateUser(user: User)
    fun insertUpdateUsers(users: List<User>)
    fun getAll(): List<User>
    fun getAllOperator(): List<User>
    fun getOperator(): User?
    fun getAllOperatorCloud(): Maybe<List<User>>
    fun getTaskOperator(): List<User>
    fun getFirst(): User?
    fun getProfile(): User?
    fun deleteAllObs(): Completable
    fun deleteAll()
    fun getCurrentUser(): User?
    fun getUser(id: Long): User?
    fun getUserCloud(userId: Long): Single<User>
    fun getCurrentUserRecruiter(): User?
    fun getAllAvailableOperator(): Maybe<List<User>>
    fun searchOperator(keyword: String): Single<List<User>>
    fun getAvailableDate(parkingSlotSalesId: Long, foodTruckId: Long): Single<List<Long>>
    fun loginPOS(params: LoginPosUseCase.Params): Single<UserAuthData>
    fun getProfileMerchant(): User?
    fun updateUser(user: User, file: File?): Completable
}