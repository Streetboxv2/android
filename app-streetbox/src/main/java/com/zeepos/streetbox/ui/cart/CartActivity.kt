package com.zeepos.streetbox.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.streetbox.R
import com.zeepos.ui_base.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_cart.*

class CartActivity : BaseActivity<CartViewEvent, CartViewModel>() {

    lateinit var cartAdapter: CartAdapter

    override fun initResourceLayout(): Int {
        return R.layout.activity_cart
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(CartViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        showLoading()
        viewModel.getCartData("00sn2")

        btn_checkout.setOnClickListener {
//            startActivity(PaymentActivity.getIntent(this, ""))
        }
    }

    private fun setList(data: MutableList<ParkingSales>) {
        cartAdapter = CartAdapter(data)

        rcv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
            cartAdapter.setEmptyView(getEmptyDataView())
        }
    }

    private fun getEmptyDataView(): View {
        val notDataView: View = layoutInflater.inflate(R.layout.empty_view, rcv, false)
        notDataView.setOnClickListener {
            showLoading()
            viewModel.getCartData("asf")
        }
        return notDataView
    }

    override fun onEvent(useCase: CartViewEvent) {
        when (useCase) {
            is CartViewEvent.GetDataSuccess -> {
                setList(useCase.data)
                dismissLoading()
            }
            is CartViewEvent.GetDataFailed -> {
                setList(ArrayList())
                dismissLoading()
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, CartActivity::class.java)
            return intent
        }
    }
}
