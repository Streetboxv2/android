package com.zeepos.ui_base.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.zeepos.models.ConstVar
import com.zeepos.models.errors.ResponseError
import com.zeepos.ui_base.views.progressdialog.LoadingView
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Created by Arif S. on 5/1/20
 */
abstract class BaseFragment<VE : BaseViewEvent, VM : BaseViewModel<VE>> : Fragment() {

    @Inject
    lateinit var viewModeFactory: DaggerViewModelFactory
    protected lateinit var viewModel: VM
    lateinit var loadingView: LoadingView

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingView = LoadingView()
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(activity as Context, initResourceLayout(), null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (::viewModel.isInitialized) {
            viewModel.errorObservable.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    when (it) {
                        is ResponseError -> {
                            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewModel.errorObservable.postValue(null)
                }
            })

            viewModel.viewEventObservable.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    onEvent(it)
                    viewModel.viewEventObservable.postValue(null)
                }
            })
        }

        onViewReady(savedInstanceState)
    }

    protected fun showDialog(
        dialogFragment: DialogFragment,
        tag: String = ConstVar.TAG_DIALOG,
        bundle: Bundle? = null
    ) {
        val ft = childFragmentManager.beginTransaction()
        val prev = childFragmentManager.findFragmentByTag(tag)

        if (prev != null) {
            ft.remove(prev)
        }

        ft.addToBackStack(tag)

        if (bundle != null)
            dialogFragment.arguments = bundle
        dialogFragment.show(ft, tag)
    }

    protected fun showLoading() {
        if (!loadingView.isShowing()) {
            loadingView.show(childFragmentManager, "")
        }
    }

    protected fun dismissLoading() {
        if (loadingView.isShowing())
            loadingView.dismiss()
    }

    abstract fun initResourceLayout(): Int

    abstract fun init()

    abstract fun onViewReady(savedInstanceState: Bundle?)

    abstract fun onEvent(useCase: VE)
}