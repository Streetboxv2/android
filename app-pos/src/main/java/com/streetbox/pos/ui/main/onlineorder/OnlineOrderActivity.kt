package com.streetbox.pos.ui.main.onlineorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.streetbox.pos.R
import com.streetbox.pos.ui.main.MainActivity
import com.streetbox.pos.ui.main.onlineorder.orderbill.OrderBillFragment
import com.streetbox.pos.ui.main.onlineorder.productsales.ProductSalesFragment
import com.zeepos.ui_base.ui.BaseActivity

class OnlineOrderActivity : BaseActivity<OnlineOrderViewEvent, OnlineOrderViewModel>() {

    private var doubleBackToExitPressedOnce = false

    override fun initResourceLayout(): Int {
        return R.layout.activity_order_online
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(OnlineOrderViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        addFragment(
            ProductSalesFragment.newInstance(),
            R.id.fl_left,
            ProductSalesFragment::class.java.simpleName
        )
        addFragment(
            OrderBillFragment.newInstance(),
            R.id.fl_right,
            OrderBillFragment::class.java.simpleName
        )
    }

    override fun onEvent(useCase: OnlineOrderViewEvent) {

    }

    override fun onBackPressed() {
        startActivity(MainActivity.getIntent(this))
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, OnlineOrderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

}
