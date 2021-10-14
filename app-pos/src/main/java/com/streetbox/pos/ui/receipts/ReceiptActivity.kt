package com.streetbox.pos.ui.receipts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.streetbox.pos.R
import com.streetbox.pos.ui.main.MainActivity
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.DateTimeUtil
import io.objectbox.BoxStore.context
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_receipt.*
import kotlinx.android.synthetic.main.fragment_payment_sales.*
import kotlinx.android.synthetic.main.fragment_payment_sales.rcv
import kotlinx.android.synthetic.main.fragment_payment_sales.sv_search
import kotlinx.android.synthetic.main.setting_list.toolbar
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Arif S. on 10/7/20
 */
class ReceiptActivity : BaseActivity<ReceiptViewEvent, ReceiptViewModel>() {
    private lateinit var receiptAdapter: ReceiptAdapter
    private var startDate: Long = DateTimeUtil.getCurrentLocalDateWithoutTime()
    private var endDate: Long = DateTimeUtil.getCurrentLocalDateWithoutTime()
    private val compositeDisposable = CompositeDisposable()

    override fun initResourceLayout(): Int {
        return R.layout.activity_receipt
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ReceiptViewModel::class.java)
        receiptAdapter = ReceiptAdapter()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(MainActivity.getIntent(this))
    }
    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        initList()

        showLoading()
        Handler().postDelayed({
            viewModel.getAllTransaction(startDate, endDate, "")
        }, 2000)

        val selectedDateDisplay =
            DateTimeUtil.getDateWithFormat(startDate, "dd/MM/YYYY")
        tv_start_date.text = selectedDateDisplay
        tv_end_date.text = selectedDateDisplay

        observeSearchView()

        sv_search.setOnClickListener {
            sv_search.isIconified = false
        }

        tv_start_date.setOnClickListener {
            selectDate(true)
        }

        tv_end_date.setOnClickListener {
            selectDate(false)
        }

        btn_cari.setOnClickListener {
//            if (!sv_search.isIconified)
//                sv_search.isIconified = true
            viewModel.getAllTransaction(startDate, endDate, "")
        }
    }

    override fun onEvent(useCase: ReceiptViewEvent) {
        when (useCase) {
            is ReceiptViewEvent.GetAllTransactionSuccess -> {
                Thread.sleep(2000)
                dismissLoading()
                receiptAdapter.setList(useCase.orderList)
            }
            is ReceiptViewEvent.VoidOrderSuccess -> {
                Toast.makeText(
                    this,
                    "Void Order Success",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.getAllTransaction(startDate, endDate, "")
            }
        }
    }

    private fun initList() {
        rcv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = receiptAdapter

            receiptAdapter.setEmptyView(R.layout.empty_data)

            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        receiptAdapter.setOnItemClickListener { adapter, view, position ->
            val order = adapter.getItem(position) as Order
            val dialog = ReceiptDetailDialog()
            val bundle = Bundle()

            bundle.putString("orderUniqueId", order.uniqueId)
            bundle.putInt("taxType", order.taxSales[0].type)
            bundle.putDouble("taxAmount", order.taxSales[0].amount)
            bundle.putString("taxName",order.taxSales[0].name)
            bundle.putBoolean("isActive",order.taxSales[0].isActive)
            dialog.arguments = bundle
            showDialog(dialog)
        }
    }

    private fun observeSearchView() {
        val disposable = fromView(sv_search)
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter {
                if (it.isEmpty()) {
                    receiptAdapter.data.clear()
                    viewModel.getAllTransaction(startDate, endDate, "")
                    return@filter false
                }

                true
            }
            .distinctUntilChanged()
            .switchMap { viewModel.searchTransaction(startDate, endDate, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                receiptAdapter.setList(it)
            }, {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
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

    private fun selectDate(isStartDate: Boolean) {

        val timezone = TimeZone.getDefault()
        val builder: MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()
        val currentTimeMillisUTC = DateTimeUtil.getCurrentDateTime()
        val currentTimeMillis = currentTimeMillisUTC + timezone.getOffset(currentTimeMillisUTC)
        val constraintBuilder = CalendarConstraints.Builder()

        constraintBuilder.setOpenAt(currentTimeMillis)

        builder.setTitleText("Select date")

        val selectedStartDate = startDate
        val selectedEndDate = endDate

        val currentActiveDate = if (isStartDate) {
            if (startDate > 0) selectedStartDate else currentTimeMillis
        } else {
            if (endDate > 0) selectedEndDate else currentTimeMillis
        }

        builder.setSelection(currentActiveDate)
        builder.setCalendarConstraints(constraintBuilder.build())

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            val selectedDateDisplay =
                DateTimeUtil.getDateWithFormat(it, DateTimeUtil.FORMAT_DATE_WITHOUT_TIME)

            if (isStartDate) {
                startDate = DateTimeUtil.getCurrentDateWithoutTime(it)
                tv_start_date?.text = selectedDateDisplay
            } else {
                endDate = DateTimeUtil.getCurrentDateWithoutTime(it)
                tv_end_date?.text = selectedDateDisplay
            }

        }
        picker.show(supportFragmentManager, picker.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, ReceiptActivity::class.java)
            return intent
        }
    }
}