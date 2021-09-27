package com.streetbox.pos.ui.main.onlineorder.productsales

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.streetbox.pos.R
import com.streetbox.pos.ui.main.MainActivity
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderViewModel
import com.streetbox.pos.ui.main.onlineorder.paymentsales.ProductSalesViewEvent
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_payment_sales.*
import java.util.concurrent.TimeUnit

class ProductSalesFragment : BaseFragment<ProductSalesViewEvent, ProductSalesViewModel>(),
    Toolbar.OnMenuItemClickListener {

    private lateinit var productSalesAdapter: ProductSalesAdapter
    private var onlineOrderViewModel: OnlineOrderViewModel? = null
    private val compositeDisposable = CompositeDisposable()

    override fun initResourceLayout(): Int {
        return R.layout.fragment_payment_sales
    }

    override fun init() {

        viewModel = ViewModelProvider(this, viewModeFactory).get(ProductSalesViewModel::class.java)

        activity?.let {
            onlineOrderViewModel = ViewModelProvider(it).get(OnlineOrderViewModel::class.java)
        }

        productSalesAdapter = ProductSalesAdapter()
        viewModel.getOnlineOrder()

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        tv_no.setText("No")
        tv_no_order.setText("Transaction Id")
        tv_no_order_bill.setText("No. Order Bill")
        tv_createTime.setText("CREATED TIME")

        initList()

        productSalesAdapter.setOnItemClickListener { adapter, _, position ->
            val order = adapter.getItem(position) as Order

            onlineOrderViewModel?.orderObserver?.postValue(order)

        }

        toolbar.setNavigationOnClickListener {
            startActivity(context?.let { it1 -> MainActivity.getIntent(it1) })
        }

        observeSearchView()

        sv_search.setOnClickListener {
            sv_search.isIconified = false
        }
    }


    private fun observeSearchView() {
        val disposable = fromView(sv_search)
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter {
                if (it.isEmpty()) {
                    productSalesAdapter.data.clear()
                    viewModel.getOnlineOrder()
                    return@filter false
                }

                true
            }
            .distinctUntilChanged()
            .switchMap { viewModel.searchOrder(it) }
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                productSalesAdapter.setList(it)
            }, {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            })

        compositeDisposable.add(disposable)
    }

    private fun fromView(searchView: SearchView): Observable<String> {
        val subject = PublishSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                subject.onComplete()
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                subject.onNext(newText)
                return false
            }
        })

        return subject
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
           /* R.id.action_sync -> {
                Toast.makeText(context, "sync", Toast.LENGTH_LONG).show()
                *//*  val i = Intent(activity, ScanningActivity::class.java)
                  activity?.startActivityForResult(i,ScanningActivity.SCANNING_FOR_PRINTER)*//*
                return true
            }*/
           /* R.id.action_select_printer -> {
                Toast.makeText(context, "tes", Toast.LENGTH_LONG).show()
                return true
            }*/
        }

        return super.onOptionsItemSelected(item)
    }


    private fun initList() {
        rcv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productSalesAdapter

            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }

    override fun onEvent(useCase: ProductSalesViewEvent) {
        when (useCase) {
            is ProductSalesViewEvent.GetAllOrderSalesSuccess -> {
            }
            is ProductSalesViewEvent.GetOnlineOrderSuccess -> {

                val order = useCase.onlineOrder.order
                if (order != null) {
                    for (i in useCase.onlineOrder.order!!.indices) {

                        useCase.onlineOrder.order!![i].no = i + 1
                    }
                    productSalesAdapter.setList(useCase.onlineOrder.order)
                }
            }
            is ProductSalesViewEvent.GetAllProductsSalesSuccess -> TODO()
            is ProductSalesViewEvent.GetAllProductSalesSuccess -> TODO()
            is ProductSalesViewEvent.GetOrderBillSuccess -> {
                onlineOrderViewModel?.orderBillObserver?.postValue(useCase.order)


            }
        }
    }

    companion object {
        fun newInstance(): ProductSalesFragment {
            return ProductSalesFragment()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        /*if (item!!.itemId == R.id.action_select_printer) {
            Toast.makeText(context, "tes", Toast.LENGTH_LONG).show()
        }*/
        return false
    }


}