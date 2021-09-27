package com.zeepos.streetbox.di

import com.zeepos.map.ui.MapActivity
import com.zeepos.map.ui.MapActivityModule
import com.zeepos.streetbox.ui.broadcast.BroadCastModule
import com.zeepos.streetbox.ui.broadcast.blast.BlastFragment
import com.zeepos.streetbox.ui.broadcast.callaccepted.AcceptedCallHistoryFragment
import com.zeepos.streetbox.ui.broadcast.callaccepted.AcceptedCallMapsActivity
import com.zeepos.streetbox.ui.broadcast.callcustomer.CustomerCallFragment
import com.zeepos.streetbox.ui.broadcast.callcustomer.CustomerCallMapsActivity
import com.zeepos.streetbox.ui.broadcast.history.HistoryAllBlastFragment
import com.zeepos.streetbox.ui.cart.CartActivity
import com.zeepos.streetbox.ui.cart.CartActivityModule
import com.zeepos.streetbox.ui.main.MainActivity
import com.zeepos.streetbox.ui.main.MainActivityModule
import com.zeepos.streetbox.ui.main.logs.LogFragment
import com.zeepos.streetbox.ui.main.logs.LogFragmentModule
import com.zeepos.streetbox.ui.main.myparkingspace.MyParkingSpaceFragment
import com.zeepos.streetbox.ui.main.myparkingspace.MyParkingSpaceFragmentModule
import com.zeepos.streetbox.ui.main.parkingspace.ParkingSpaceFragment
import com.zeepos.streetbox.ui.main.parkingspace.ParkingSpaceFragmentModule
import com.zeepos.streetbox.ui.main.profile.ProfileFragment
import com.zeepos.streetbox.ui.main.profile.ProfileFragmentModule
import com.zeepos.streetbox.ui.operator.OperatorHomeFragment
import com.zeepos.streetbox.ui.operator.OperatorHomeModule
import com.zeepos.streetbox.ui.operator.main.OperatorFTActivity
import com.zeepos.streetbox.ui.operator.main.OperatorFTModule
import com.zeepos.streetbox.ui.operator.main.OperatorMainActivity
import com.zeepos.streetbox.ui.operator.main.OperatorMainModule
import com.zeepos.streetbox.ui.operator.operatortask.OperatorTaskFragment
import com.zeepos.streetbox.ui.operator.operatortask.OperatorTaskModule
import com.zeepos.streetbox.ui.operatorfreetask.OperatorFreeTaskFragment
import com.zeepos.streetbox.ui.operatorfreetask.OperatorFreeTaskModule
import com.zeepos.streetbox.ui.operatormerchant.OperatorActivity
import com.zeepos.streetbox.ui.operatormerchant.OperatorActivityModule
import com.zeepos.streetbox.ui.parkingdetail.ParkingDetailActivity
import com.zeepos.streetbox.ui.parkingdetail.ParkingDetailActivityModule
import com.zeepos.ui_base.di.ActivityScope
import com.zeepos.ui_base.di.FragmentScope
import com.zeepos.ui_login.LoginActivity
import com.zeepos.ui_login.LoginActivityModule
import com.zeepos.ui_password.ForgotPasswordActivity
import com.zeepos.ui_password.ForgotPasswordModule
import com.zeepos.ui_splashscreen.SplashActivity
import com.zeepos.ui_splashscreen.SplashScreenActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Arif S. on 5/2/20
 */
@Module
internal abstract class ActivitiesModule {


    @ActivityScope
    @ContributesAndroidInjector(modules = [SplashScreenActivityModule::class])
    internal abstract fun splashActivity(): SplashActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [LoginActivityModule::class])
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ParkingDetailActivityModule::class])
    internal abstract fun parkingDetailActivity(): ParkingDetailActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ForgotPasswordModule::class])
    internal abstract fun forgotPasswordActivity(): ForgotPasswordActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [CartActivityModule::class])
    abstract fun cartActivity(): CartActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [OperatorActivityModule::class])
    abstract fun operatorActivity(): OperatorActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [OperatorMainModule::class])
    abstract fun operatorMainActivity(): OperatorMainActivity


    @ActivityScope
    @ContributesAndroidInjector(modules = [MapActivityModule::class])
    abstract fun mapActivity(): MapActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [OperatorFTModule::class])
    abstract fun operatorFTActivity(): OperatorFTActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [OperatorTaskModule::class])
    abstract fun operatorTaskFragment(): OperatorTaskFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [OperatorFreeTaskModule::class])
    abstract fun operatorFreeTaskFragment(): OperatorFreeTaskFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [OperatorHomeModule::class])
    abstract fun operatorHomeFragment(): OperatorHomeFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ParkingSpaceFragmentModule::class])
    abstract fun parkingSpaceFragment(): ParkingSpaceFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    abstract fun profileFragment(): ProfileFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [MyParkingSpaceFragmentModule::class])
    abstract fun myParkingSpaceFragment(): MyParkingSpaceFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [LogFragmentModule::class])
    abstract fun logFragment(): LogFragment

    @ActivityScope
    @ContributesAndroidInjector(modules = [BroadCastModule::class])
    internal abstract fun callAcceptedActivity(): AcceptedCallMapsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [BroadCastModule::class])
    internal abstract fun customerCallMaps(): CustomerCallMapsActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [BroadCastModule::class])
    abstract fun acceptedHistoryFragment(): AcceptedCallHistoryFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [BroadCastModule::class])
    abstract fun callCustomerFragment(): CustomerCallFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [BroadCastModule::class])
    abstract fun historyAllBlast(): HistoryAllBlastFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [BroadCastModule::class])
    abstract fun blastFragment(): BlastFragment
}