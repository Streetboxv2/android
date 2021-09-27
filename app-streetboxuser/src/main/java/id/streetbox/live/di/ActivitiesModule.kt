package id.streetbox.live.di

import com.zeepos.map.ui.MapActivity
import com.zeepos.map.ui.MapActivityModule
import com.zeepos.map.ui.merchantinstagram.MerchantInstagramFragment
import com.zeepos.map.ui.merchantinstagram.MerchantInstagramFragmentModule
import com.zeepos.map.ui.merchantschedule.MerchantScheduleFragment
import com.zeepos.map.ui.merchantschedule.MerchantScheduleFragmentModule
import com.zeepos.payment.PaymentActivity
import com.zeepos.payment.PaymentActivityModule
import com.zeepos.ui_base.di.ActivityScope
import com.zeepos.ui_base.di.FragmentScope
import com.zeepos.ui_login.LoginActivity
import com.zeepos.ui_login.LoginActivityModule
import com.zeepos.ui_splashscreen.SplashActivity
import com.zeepos.ui_splashscreen.SplashScreenActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.streetbox.live.ui.bookhomevisit.BookHomeVisitActivity
import id.streetbox.live.ui.bookhomevisit.BookHomeVisitActivityModule
import id.streetbox.live.ui.editprofile.EditProfileActivity
import id.streetbox.live.ui.editprofile.EditProfileActivityModule
import id.streetbox.live.ui.firsttimescreen.FirstTimeActivity
import id.streetbox.live.ui.firsttimescreen.FirstTimeActivityModule
import id.streetbox.live.ui.main.MainActivity
import id.streetbox.live.ui.main.MainActivityModule
import id.streetbox.live.ui.main.address.AddAddressActivity
import id.streetbox.live.ui.main.address.AddressDeliveryActivity
import id.streetbox.live.ui.main.address.AddressDeliveryModule
import id.streetbox.live.ui.main.cart.CartFragment
import id.streetbox.live.ui.main.cart.CartFragmentModule
import id.streetbox.live.ui.main.doortodoor.DoortoDoorFragment
import id.streetbox.live.ui.main.doortodoor.DoortoDoorModule
import id.streetbox.live.ui.main.doortodoor.mycall.MyCallFragment
import id.streetbox.live.ui.main.doortodoor.notification.NotificationDoortoDoorFragment
import id.streetbox.live.ui.main.doortodoor.notification.NotificationDoortoDoorMapsActivity
import id.streetbox.live.ui.main.home.HomeFragment
import id.streetbox.live.ui.main.home.HomeFragmentModule
import id.streetbox.live.ui.main.home.homevisit.HomeVisitFragment
import id.streetbox.live.ui.main.home.homevisit.HomeVisitFragmentModule
import id.streetbox.live.ui.main.home.nearby.NearByFragment
import id.streetbox.live.ui.main.home.nearby.NearByFragmentModule
import id.streetbox.live.ui.main.home.nearby.NearbyDetailVisitActivity
import id.streetbox.live.ui.main.orderhistory.OrderHistoryFragment
import id.streetbox.live.ui.main.orderhistory.OrderHistoryFragmentModule
import id.streetbox.live.ui.main.orderhistory.history.HistoryFragment
import id.streetbox.live.ui.main.orderhistory.history.HistoryFragmentModule
import id.streetbox.live.ui.main.orderhistory.ongoing.OnGoingFragment
import id.streetbox.live.ui.main.orderhistory.orderhistorydetail.OrderHistoryDetailActivity
import id.streetbox.live.ui.main.orderhistory.orderhistorydetail.OrderHistoryDetailActivityModule
import id.streetbox.live.ui.main.profile.ProfileFragment
import id.streetbox.live.ui.main.profile.ProfileFragmentModule
import id.streetbox.live.ui.menu.MenuActivity
import id.streetbox.live.ui.menu.MenuActivityModule
import id.streetbox.live.ui.orderreview.homevisit.BookHomeVisitOrderActivity
import id.streetbox.live.ui.orderreview.homevisit.BookHomeVisitOrderActivityModule
import id.streetbox.live.ui.orderreview.pickup.PickUpOrderReviewActivityModule
import id.streetbox.live.ui.orderreview.pickup.PickupOrderReviewActivity
import id.streetbox.live.ui.pickuporder.PickupOrderActivity
import id.streetbox.live.ui.pickuporder.PickupOrderActivityModule
import id.streetbox.live.ui.termsconditions.TermConditionActivity
import id.streetbox.live.ui.termsconditions.TermConditionActivityModule

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
    @ContributesAndroidInjector(modules = [PickupOrderActivityModule::class])
    internal abstract fun pickUpOrderActvitiy(): PickupOrderActivity


    @ActivityScope
    @ContributesAndroidInjector(modules = [MenuActivityModule::class])
    internal abstract fun nearbyDetailVisit(): NearbyDetailVisitActivity


    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [EditProfileActivityModule::class])
    internal abstract fun editProfileActivity(): EditProfileActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MapActivityModule::class])
    abstract fun mapActivity(): MapActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MenuActivityModule::class])
    abstract fun menuActivity(): MenuActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [BookHomeVisitActivityModule::class])
    abstract fun bookHomeVisitActivity(): BookHomeVisitActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PickUpOrderReviewActivityModule::class])
    abstract fun pickupOrderReviewActivity(): PickupOrderReviewActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PaymentActivityModule::class])
    internal abstract fun paymentActivity(): PaymentActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [FirstTimeActivityModule::class])
    internal abstract fun firstTimeActivity(): FirstTimeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [BookHomeVisitOrderActivityModule::class])
    internal abstract fun bookHomeVisitOrderActivity(): BookHomeVisitOrderActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [OrderHistoryDetailActivityModule::class])
    internal abstract fun orderHistoryDetailActivity(): OrderHistoryDetailActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    abstract fun profileFragment(): ProfileFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [DoortoDoorModule::class])
    abstract fun doorToDoorFragment(): DoortoDoorFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TermConditionActivityModule::class])
    abstract fun termConditionActivity(): TermConditionActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun homeFragment(): HomeFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [NearByFragmentModule::class])
    abstract fun nearByFragment(): NearByFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [HomeVisitFragmentModule::class])
    abstract fun homeVisitFragment(): HomeVisitFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [OrderHistoryFragmentModule::class])
    abstract fun orderHistoryFragment(): OrderHistoryFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [HistoryFragmentModule::class])
    abstract fun historyFragment(): HistoryFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [HistoryFragmentModule::class])
    abstract fun ongoingFragment(): OnGoingFragment


    @FragmentScope
    @ContributesAndroidInjector(modules = [CartFragmentModule::class])
    abstract fun cartFragment(): CartFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [MerchantScheduleFragmentModule::class])
    abstract fun merchantScheduleFragmentModule(): MerchantScheduleFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [MerchantInstagramFragmentModule::class])
    abstract fun merchantInstagramFragment(): MerchantInstagramFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AddressDeliveryModule::class])
    abstract fun deliveryAddressActivity(): AddressDeliveryActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [AddressDeliveryModule::class])
    abstract fun addAddressActivity(): AddAddressActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [DoortoDoorModule::class])
    abstract fun myCallFragment(): MyCallFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [DoortoDoorModule::class])
    abstract fun notifBlastFragment(): NotificationDoortoDoorFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [DoortoDoorModule::class])
    abstract fun notifMapsActiviyt(): NotificationDoortoDoorMapsActivity

}