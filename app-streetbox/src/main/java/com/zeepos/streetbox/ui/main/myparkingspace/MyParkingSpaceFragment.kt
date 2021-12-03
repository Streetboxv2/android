package com.zeepos.streetbox.ui.main.myparkingspace

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeepos.map.ui.MapActivity
import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.operatormerchant.OperatorActivity
import com.zeepos.streetbox.ui.parkingdetail.ParkingDetailActivity
import com.zeepos.ui_base.ui.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.parkingspace_fragment.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Arif S. on 5/21/20
 */
class MyParkingSpaceFragment : BaseFragment<MyParkingSpaceViewEvent, MyParkingSpaceViewModel>() {

    private lateinit var myParkingSpaceAdapter: MyParkingSpaceAdapter
    private var foodtruck:List<User> = ArrayList()
    private val compositeDisposable = CompositeDisposable()
    private var isLoadMore = true
    private var id: Long= 0L

    override fun initResourceLayout(): Int {
        return R.layout.my_parking_space_fragment
    }

    override fun init() {
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(MyParkingSpaceViewModel::class.java)
        myParkingSpaceAdapter = MyParkingSpaceAdapter()
//        viewModel.getParkingSales()//if use cache uncomment this
        viewModel.getParkingSalesCloud(true)

    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        swipe_refresh.setColorSchemeColors(Color.rgb(47, 223, 189))
        swipe_refresh.isRefreshing = true
        swipe_refresh.setOnRefreshListener {
            viewModel.getParkingSalesCloud(true)
        }

        rcv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myParkingSpaceAdapter
        }

        myParkingSpaceAdapter.loadMoreModule.setOnLoadMoreListener {

            if (isLoadMore) {
                viewModel.getParkingSalesCloud()
            } else {
                myParkingSpaceAdapter.loadMoreModule.loadMoreEnd()
            }
        }

        myParkingSpaceAdapter.setOnItemClickListener { adapter, _, position ->
            context?.let {
                val parkingSales = adapter.getItem(position) as ParkingSales
                if (parkingSales.tasksId > 0)
                    Toast.makeText(context, "Already assign", Toast.LENGTH_SHORT).show()
                else {
                    if (parkingSales.trxVisitSalesId > 0) {
                        id = parkingSales.trxVisitSalesId
                        startActivity(
                                OperatorActivity.getIntent(
                                        it,
                                        id, id
                                )
                        )
                    } else {
                        id = parkingSales.id
                        startActivity(ParkingDetailActivity.getIntent(it, id, true))
                    }
                }
            }
        }

        myParkingSpaceAdapter.setOnItemChildClickListener { adapter, view, position ->
            context?.let {
                val parkingSales = adapter.getItem(position) as ParkingSales
                if (parkingSales.platNo != "") {
                    context?.let {
                        foodtruck.forEach {
                            if (parkingSales.tasksId == it.tasksId) {
                                showMap(it)
                            }
                        }
//                    startActivity(OperatorActivity.getIntent(it, -1,parkingSales.trxVisitSalesId))
                    }
                }
            }

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
                    myParkingSpaceAdapter.data.clear()
                    viewModel.getParkingSalesCloud(true)
                    isLoadMore = true
                    return@filter false
                }

                true
            }
            .distinctUntilChanged()
            .switchMap { viewModel.searchParkingSpace(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                myParkingSpaceAdapter.setList(it)
                isLoadMore = false
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

    override fun onEvent(useCase: MyParkingSpaceViewEvent) {
        when (useCase) {
            is MyParkingSpaceViewEvent.GetAllParkingSalesSuccess -> {
                if (useCase.data.isNotEmpty()) {
                    viewModel.getAllOperator()
                    if (swipe_refresh.isRefreshing) {
                        myParkingSpaceAdapter.data.clear()
                    }


//                    myParkingSpaceAdapter.addData(useCase.data)
                    myParkingSpaceAdapter.setList(useCase.data)//currently api not support load more
                    myParkingSpaceAdapter.loadMoreModule.loadMoreComplete()
//                    myParkingSpaceAdapter.loadMoreModule.loadMoreEnd()
                } else {
                    myParkingSpaceAdapter.loadMoreModule.loadMoreEnd()
                }

                swipe_refresh.isRefreshing = false
                dismissLoading()
            }
            is MyParkingSpaceViewEvent.GetAllParkingSpaceFailed -> {
                myParkingSpaceAdapter.loadMoreModule.loadMoreFail()
                swipe_refresh.isRefreshing = false
                dismissLoading()
                Toast.makeText(context, useCase.errorMessage, Toast.LENGTH_SHORT).show()
            }
            is MyParkingSpaceViewEvent.GetTaskOperatorSuccess -> {
                if (useCase.data.isNotEmpty()) {
                    val parkingSalesId = useCase.data[0].parkingSalesId
                    context?.let {
                        startActivity(
                            MapActivity.getIntent(
                                it,
                                parkingSalesId,
                                ConstVar.MAP_TYPE_LIVE_TRACK,
                                0,
                                0,
                                "",
                                ""
                            )
                        )

                    }
                } else {
                    Toast.makeText(
                        context,
                        "No task in this parking space",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                dismissLoading()
            }
            MyParkingSpaceViewEvent.GetTaskOperatorFailed -> {
                Toast.makeText(
                    context,
                    "No task found!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                dismissLoading()
            }
            is MyParkingSpaceViewEvent.GetOperatorSuccess -> {
                        foodtruck = useCase.data!!
            }
            is MyParkingSpaceViewEvent.GetOperatorFailed -> {

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
                context!!,
                type = mapType,
                bundle = bundle
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    companion object {
        fun newInstance(): MyParkingSpaceFragment {
            return MyParkingSpaceFragment()
        }
    }
}