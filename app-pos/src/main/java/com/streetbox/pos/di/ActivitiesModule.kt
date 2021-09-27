package com.streetbox.pos.di

import com.streetbox.pos.ui.checkout.CheckoutActivity
import com.streetbox.pos.ui.checkout.CheckoutActivityModule
import com.streetbox.pos.ui.checkout.checkoutdetail.CheckoutDetailFragment
import com.streetbox.pos.ui.checkout.checkoutdetail.CheckoutDetailFragmentModule
import com.streetbox.pos.ui.login.LoginActivity
import com.streetbox.pos.ui.login.LoginActivityModule
import com.streetbox.pos.ui.main.MainActivity
import com.streetbox.pos.ui.main.MainActivityModule
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderActivity
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderActivityModule
import com.streetbox.pos.ui.main.onlineorder.orderbill.OrderBillFragment
import com.streetbox.pos.ui.main.onlineorder.orderbill.OrderBillFragmentModule
import com.streetbox.pos.ui.main.onlineorder.paymentsales.ProductSalesFragmentModule
import com.streetbox.pos.ui.main.onlineorder.productsales.ProductSalesFragment
import com.streetbox.pos.ui.main.order.OrderFragment
import com.streetbox.pos.ui.main.order.OrderFragmentModule
import com.streetbox.pos.ui.main.print.PrintSettingActivity
import com.streetbox.pos.ui.main.print.PrintSettingActivityModule
import com.streetbox.pos.ui.main.product.ProductFragment
import com.streetbox.pos.ui.main.product.ProductFragmentModule
import com.streetbox.pos.ui.receipts.ReceiptActivity
import com.streetbox.pos.ui.receipts.ReceiptActivityModule
import com.streetbox.pos.ui.setting.SettingActivity
import com.streetbox.pos.ui.setting.SettingActivityModule
import com.streetbox.pos.ui.setting.printersetting.PrinterSettingFragment
import com.streetbox.pos.ui.setting.printersetting.PrinterSettingFragmentModule
import com.zeepos.payment.PaymentActivity
import com.zeepos.payment.PaymentActivityModule
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
    @ContributesAndroidInjector(modules = [LoginActivityModule::class])
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [OnlineOrderActivityModule::class])
    internal abstract fun onlineOrderActivity(): OnlineOrderActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PaymentActivityModule::class])
    internal abstract fun paymentActivity(): PaymentActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [CheckoutActivityModule::class])
    internal abstract fun checkoutActivity(): CheckoutActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PrintSettingActivityModule::class])
    internal abstract fun printSettingActivity(): PrintSettingActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SettingActivityModule::class])
    internal abstract fun settingActivity(): SettingActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ReceiptActivityModule::class])
    internal abstract fun receiptActivity(): ReceiptActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProductFragmentModule::class])
    internal abstract fun productFragment(): ProductFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [OrderFragmentModule::class])
    internal abstract fun orderFragment(): OrderFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProductSalesFragmentModule::class])
    internal abstract fun productSalesFragment(): ProductSalesFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [OrderBillFragmentModule::class])
    internal abstract fun orderBillFragment(): OrderBillFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CheckoutDetailFragmentModule::class])
    internal abstract fun checkoutDetailFragment(): CheckoutDetailFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [PrinterSettingFragmentModule::class])
    internal abstract fun printerSettingFragment(): PrinterSettingFragment

}