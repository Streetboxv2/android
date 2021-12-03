package com.zeepos.streetbox.ui.operatormerchant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.zeepos.map.ui.MapActivity
import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.dialog.FoodTruckDetailDialog
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_base.views.ActiveDateValidator
import com.zeepos.utilities.DateTimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_food_truck.*
import java.util.*
import java.util.concurrent.TimeUnit


class OperatorActivity : BaseActivity<OperatorViewEvent, OperatorViewModel>() {

    private lateinit var operatorAdapter: OperatorAdapter
    private var parkingSlotSalesId: Long = 0
    private var trxVisitSalesId: Long = 0
    private val compositeDisposable = CompositeDisposable()

    override fun initResourceLayout(): Int {
        return R.layout.activity_food_truck
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(OperatorViewModel::class.java)

        parkingSlotSalesId = intent.getLongExtra("id", 0)
        trxVisitSalesId = intent.getLongExtra("trxVisitSalesId", 0)
        operatorAdapter = OperatorAdapter()
        operatorAdapter.isAssignEnabled = parkingSlotSalesId > 0
        operatorAdapter.isLiveTracking = parkingSlotSalesId < 0
        showLoading()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllOperator()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        initList()

        observeSearchView()

        sv_search.setOnClickListener {
            sv_search.isIconified = false
        }

    }

    private fun initList() {
        rv_operator.apply {
            layoutManager = LinearLayoutManager(this@OperatorActivity)
            adapter = operatorAdapter
            setHasFixedSize(true)
        }

        operatorAdapter.setOnItemClickListener { adapter, view, position ->
            val user = adapter.getItem(position) as User
            val bundle = Bundle()

            bundle.putLong("id", user.id)
            showDialog(FoodTruckDetailDialog(), bundle = bundle)
        }

        operatorAdapter.setOnItemChildClickListener { adapter, view, position ->
            val foodTruck: User = adapter.getItem(position) as User

            when (view.id) {
                R.id.tv_assign -> {
                    if(trxVisitSalesId > 0){
                        viewModel.createOperatorTaskHomeVisit(trxVisitSalesId,foodTruck)

                    }else{
                        selectDate(foodTruck)
                    }

                }
                R.id.btn_tracking -> {
                    if (foodTruck.id > 0) {
                        showMap(foodTruck)
                    } else {
                        Toast.makeText(this, "No task for this food truck", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }

    }

    private fun showMap(foodTruck: User) {
        val bundle = Bundle()
        bundle.putLong("taskId", foodTruck.tasksId)

        val mapType = if (foodTruck.status == ConstVar.TASK_STATUS_IN_PROGRESS) {
            ConstVar.MAP_TYPE_LIVE_TRACK
        } else {
            ConstVar.MAP_TYPE_CHECK_IN_LOCATION
        }

        startActivity(
            MapActivity.getIntent(
                this,
                type = mapType,
                bundle = bundle
            )
        )
    }

    private fun selectDate(foodTruck: User) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.clear()

        val parkingSlotSales = viewModel.getParkingSlotSales(parkingSlotSalesId)

        val startDateMillis =
            DateTimeUtil.getDateFromString(parkingSlotSales?.startDate)?.time ?: 0L
        val endDateMillis =
            DateTimeUtil.getDateFromString(parkingSlotSales?.endDate)?.time ?: 0L

        calendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
        calendar.roll(Calendar.MONTH, 1)

        val builder: MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()
        val currentTimeMillis = Calendar.getInstance().timeInMillis

        builder.setTitleText("Select date")
        builder.setSelection(currentTimeMillis)

        val constraintBuilder = CalendarConstraints.Builder()
        constraintBuilder.setStart(currentTimeMillis)
        constraintBuilder.setEnd(endDateMillis)
        constraintBuilder.setOpenAt(currentTimeMillis)

        val activeDate: List<Long> =
            viewModel.getParkingSlotAvailableDate(parkingSlotSalesId, foodTruck.id)

        constraintBuilder.setValidator(ActiveDateValidator(activeDate))

        builder.setCalendarConstraints(constraintBuilder.build())

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            val scheduleDate = DateTimeUtil.getDateWithFormat(it, "yyyy-MM-dd")
            showLoading()
            viewModel.createOperatorTask(parkingSlotSalesId, foodTruck, scheduleDate)
        }
        picker.show(supportFragmentManager, picker.toString())
    }

    private fun observeSearchView() {
        val disposable = fromView(sv_search)
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter {
                if (it.isEmpty()) {
                    operatorAdapter.data.clear()
                    viewModel.getAllOperator()
                    return@filter false
                }

                true
            }
            .distinctUntilChanged()
            .switchMap { viewModel.searchFoodTruck(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ operatorAdapter.setList(it) }, {
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

    override fun onEvent(useCase: OperatorViewEvent) {
        when (useCase) {
            is OperatorViewEvent.GetOperatorSuccess -> {
                val list: MutableList<User> = ArrayList()
                useCase.data?.let {
                    useCase.data.forEach {
                        if(it.tasksId < 1){
                            list.add(it)
                        }

                    }

                }
                operatorAdapter.setList(list)
                dismissLoading()

            }

            is OperatorViewEvent.GetOperatorFailed -> {
                dismissLoading()
            }
            OperatorViewEvent.CreateTaskOperatorSuccess -> {
                dismissLoading()
                showDialogSuccess()
            }
            is OperatorViewEvent.CreateTaskOperatorFailed -> {
                dismissLoading()
                Toast.makeText(this, useCase.errorMessage, Toast.LENGTH_SHORT).show()
            }
            OperatorViewEvent.CreateTaskOperatorHomeVisitSuccess ->  {
                dismissLoading()
                showDialogSuccess()
            }
            is OperatorViewEvent.CreateTaskOperatorHomeVisitFailed -> {
                dismissLoading()
                Toast.makeText(this, useCase.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialogSuccess() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Assign task success")
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { p0, _ ->
            p0?.dismiss()
            finish()
        }
        alertDialogBuilder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        fun getIntent(context: Context, parkingSlotId: Long = 0, trxVisitSalesId:Long = 0): Intent {
            val intent = Intent(context, OperatorActivity::class.java)
            intent.putExtra("id", parkingSlotId)
            intent.putExtra("trxVisitSalesId", trxVisitSalesId)
            return intent
        }
    }
}
