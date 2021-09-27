package com.zeepos.localstorage

import com.zeepos.domain.repository.*
import dagger.Binds
import dagger.Module

/**
 * Created by Arif S. on 5/2/20
 */
@Module
abstract class LocalRepositoryModule {
    @Binds
    protected abstract fun userRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    protected abstract fun localPreferenceRepository(localPreferenceRepositoryImpl: LocalPreferenceRepositoryImpl): LocalPreferencesRepository

    @Binds
    protected abstract fun parkingSpaceRepository(parkingSpaceRepositoryImpl: ParkingSpaceRepositoryImpl): ParkingSpaceRepository

    @Binds
    protected abstract fun parkingSalesRepo(parkingSalesRepoImpl: ParkingSalesRepoImpl): ParkingSalesRepo

    @Binds
    protected abstract fun orderRepo(orderRepoImpl: OrderRepoImpl): OrderRepo

    @Binds
    protected abstract fun parkingSlotRepo(parkingSlotRepoImpl: ParkingSlotRepoImpl): ParkingSlotRepo

    @Binds
    protected abstract fun paymentMethodRepo(paymentMethodRepoImpl: PaymentMethodRepoImpl): PaymentMethodRepo

    @Binds
    protected abstract fun parkingSlotSalesRepo(parkingSlotSalesRepoImpl: ParkingSlotSalesRepoImpl): ParkingSlotSalesRepo

    @Binds
    protected abstract fun taskOperatorRepo(taskOperatorRepoImpl: TaskOperatorRepoImpl): TaskOperatorRepo

    @Binds
    protected abstract fun mapRepo(mapRepoImpl: MapRepoImpl): MapRepo

    @Binds
    protected abstract fun parkingOperatorTaskImpl(parkingOperatorTaskImpl: ParkingOperatorTaskImpl): ParkingOperatorTaskRepository

    @Binds
    protected abstract fun syncDataRepo(syncDataRepoImpl: SyncDataRepoImpl): SyncDataRepo

    @Binds
    protected abstract fun logsRepo(logsRepoImpl: LogsRepoImpl): LogsRepo

    @Binds
    protected abstract fun productSalesRepo(productSalesRepoImpl: ProductSalesRepoImpl): ProductSalesRepo

    @Binds
    protected abstract fun orderBillRepo(orderBillRepoImpl: OrderBillRepoImpl): OrderBillRepo

    @Binds
    protected abstract fun productRepo(productRepoImpl: ProductRepoImpl): ProductRepo

    @Binds
    protected abstract fun foodTruckRepo(foodTruckRepoImpl: FoodTruckRepoImpl): FoodTruckRepo

    @Binds
    protected abstract fun paymentSalesRepo(paymentSalesRepoImpl: PaymentSalesRepoImpl): PaymentSalesRepo

    @Binds
    protected abstract fun addressRepo(addressRepoImpl: AddressRepoImpl): AddressRepo


}