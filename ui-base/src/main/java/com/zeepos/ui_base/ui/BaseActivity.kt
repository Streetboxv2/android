package com.zeepos.ui_base.ui

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.zeepos.models.ConstVar
import com.zeepos.models.errors.ResponseError
import com.zeepos.ui_base.views.progressdialog.LoadingView
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by Arif S. on 5/1/20
 */
abstract class BaseActivity<VE : BaseViewEvent, VM : BaseViewModel<VE>> :
    DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModeFactory: DaggerViewModelFactory
    protected lateinit var viewModel: VM
    lateinit var loadingView: LoadingView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        loadingView = LoadingView()
        init()
        setContentView(initResourceLayout())
        onViewReady(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        if (::viewModel.isInitialized) {
            viewModel.errorObservable.observe(this, Observer {
                if (it != null) {
                    when (it) {
                        is ResponseError -> {
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewModel.errorObservable.postValue(null)
                }
            })

            viewModel.viewEventObservable.observe(this, Observer {
                if (it != null) {
                    onEvent(it)
                    viewModel.viewEventObservable.postValue(null)
                }
            })
        }
    }

    protected fun addFragment(
        fragment: Fragment,
        container: Int,
        tag: String? = ConstVar.TAG_FRAGMENT,
        latitude: Double,
        longitude: Double,
        type : Int
    ) {
        val ts = supportFragmentManager.beginTransaction()
        val args = Bundle()
        args!!.putDouble("lat", latitude);
        args!!.putDouble("lon", longitude);
        args!!.putInt("type", type)
        fragment.arguments = args
        ts.add(container, fragment, tag)
        ts.commitNowAllowingStateLoss()
    }

    protected fun addFragment(
        fragment: Fragment,
        container: Int,
        tag: String? = ConstVar.TAG_FRAGMENT
    ) {
        val ts = supportFragmentManager.beginTransaction()
        ts.add(container, fragment, tag)
        ts.commitNowAllowingStateLoss()
    }

    protected fun replaceFragment(
        fragment: Fragment,
        container: Int,
        tag: String? = ConstVar.TAG_FRAGMENT
    ) {
        val ts = supportFragmentManager.beginTransaction()
        ts.replace(container, fragment, tag)
        ts.commitNowAllowingStateLoss()
    }

    protected fun showDialog(
        dialogFragment: DialogFragment,
        tag: String = ConstVar.TAG_DIALOG,
        bundle: Bundle? = null
    ) {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(tag)

        if (prev != null) {
            ft.remove(prev)
        }

        ft.addToBackStack(tag)

        if (bundle != null)
            dialogFragment.arguments = bundle
        dialogFragment.show(ft, tag)
    }

    protected fun showDialog(
        dialogFragment: DialogFragment,
        tag: String?,
        address: String?,
        startDate: String?,
        endDate: String?
    ) {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(tag)

        if (prev != null) {
            ft.remove(prev)
        }

        ft.addToBackStack(tag)
        val args = Bundle()
        args!!.putString("address", address);
        args!!.putString("start_date", startDate);
        args!!.putString("end_date", endDate);
        dialogFragment.setArguments(args)
        dialogFragment.show(ft, tag)
    }

    protected fun showLoading() {
        if (!loadingView.isShowing()) {
            loadingView.show(supportFragmentManager, "")
        }
    }

    protected fun dismissLoading() {
        if (loadingView.isShowing())
            loadingView.dismiss()
    }

    /**
     * Initial layout source
     */
    abstract fun initResourceLayout(): Int

    /**
     * To initial everything db, object etc..
     * that not depend on view
     */
    abstract fun init()

    /**
     * Everything object that depend on view
     */
    abstract fun onViewReady(savedInstanceState: Bundle?)

    /**
     * Listener triggered from ViewModel
     */
    abstract fun onEvent(useCase: VE)
}