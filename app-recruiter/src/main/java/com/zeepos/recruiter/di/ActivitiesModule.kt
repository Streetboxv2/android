package com.zeepos.recruiter.di

import com.zeepos.recruiter.ui.main.RecruitMainActivity
import com.zeepos.recruiter.ui.main.RecruitMainActivityModule
import com.zeepos.recruiter.ui.main.form.FormParkingSpaceActivity
import com.zeepos.recruiter.ui.main.form.FormParkingSpaceActivityModule
import com.zeepos.recruiter.ui.main.parkingspace.ParkingSpaceMasterFragment
import com.zeepos.recruiter.ui.main.parkingspace.ParkingSpaceMasterFragmentModule
import com.zeepos.recruiter.ui.main.profile.ProfileFragment
import com.zeepos.recruiter.ui.main.profile.ProfileFragmentModule
import com.zeepos.ui_base.di.ActivityScope
import com.zeepos.ui_base.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Arif S. on 5/2/20
 */
@Module
internal abstract class ActivitiesModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [RecruitMainActivityModule::class])
    internal abstract fun mainActivity(): RecruitMainActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [ParkingSpaceMasterFragmentModule::class])
    abstract fun parkingSpaceFragment(): ParkingSpaceMasterFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [FormParkingSpaceActivityModule::class])
    abstract fun formparkingspaceActivity(): FormParkingSpaceActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    abstract fun profileFragment(): ProfileFragment



}