package id.streetbox.live.ui.main.doortodoor.mycall

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zeepos.models.response.DataAddress
import com.zeepos.models.response.DataItemGetStatusCall
import com.zeepos.models.response.ResponseError
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.*
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterMenuStatusCall
import id.streetbox.live.adapter.AdapterStatusCallEndUser
import id.streetbox.live.ui.main.address.AddressDeliveryActivity
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewEvent
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewModel
import id.streetbox.live.ui.main.doortodoor.notification.NotificationDoortoDoorMapsActivity
import id.streetbox.live.ui.onclick.OnClickItemAny
import id.streetbox.live.utils.showErrorMessageThrowable
import kotlinx.android.synthetic.main.fragment_my_call.*
import retrofit2.adapter.rxjava2.HttpException
import java.io.IOException
import kotlin.math.exp

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyCallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyCallFragment : BaseFragment<DoortoDoorViewEvent, DoortoDoorViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var dataAddress: DataAddress? = null
    var launchSomeActivity: ActivityResultLauncher<Intent>? = null
    var accept: String = "Accept"
    var expired: String = "Expired"
    var onprocess: String = "On Process"
    var finish: String = "Finish"

    val onClickItemAny = object : OnClickItemAny {
        override fun clickItem(any: Any) {
            showLoading()
            val title = any as String
//            if (title.equals(accept, ignoreCase = true)) {
//            } else if (title.equals(expired, ignoreCase = true)) {
//                viewModel.callGetStatusCallEndUser("expired")
//            } else if (title.equals(onprocess, ignoreCase = true)) {
//                viewModel.callGetStatusCallEndUser("onprocess")
//            } else if (title.equals(finish, ignoreCase = true)) {
//                viewModel.callGetStatusCallEndUser("finish")
//            }
        }
    }

    val onClickItemAnyList = object : OnClickItemAny {
        override fun clickItem(any: Any) {
            val dataItemGetStatusCall = any as DataItemGetStatusCall
            val intent =
                intentPageData(requireContext(), NotificationDoortoDoorMapsActivity::class.java)
                    .putExtra("dataStatus", dataItemGetStatusCall)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        launchSomeActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == 10) {
                    val data: Intent? = result.data
                    dataAddress =
                        data?.getParcelableExtra("data")

                    showView(llAddressStatusCall)
                    tvItemNameAddressSelectAccept.text = dataAddress?.addressName
                    tvItemAddressSelectAccept.text = dataAddress?.address
                    tvItemPesonAddressAccept.text = dataAddress?.person
                    tvItemPhoneAddressSelectAccept.text = dataAddress?.phone
                }
            }
    }

    override fun initResourceLayout(): Int {
        return R.layout.fragment_my_call
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(DoortoDoorViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initial()
    }


    private fun initial() {
        setUpMenuStatusCall()
        initOnClick()
        selectAddressChange()
    }

    private fun initOnClick() {

    }

    override fun onResume() {
        super.onResume()
        viewModel.callGetStatusCallEndUser("")
    }

    private fun selectAddressChange() {
        tvChangeStatusCall.setOnClickListener {
            val intent = Intent(requireContext(), AddressDeliveryActivity::class.java)
                .putExtra("from", "homevisit")
            launchSomeActivity?.launch(intent)
        }
    }


    private fun setUpMenuStatusCall() {
        val listMenu: MutableList<String> = arrayListOf()
        listMenu.addAll(listOf(accept, expired, onprocess, finish))
        val adapterMenuStatusCall = AdapterMenuStatusCall(listMenu, onClickItemAny)

        rvStatusCall.apply {
            adapter = adapterMenuStatusCall
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            hasFixedSize()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyCallFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onEvent(useCase: DoortoDoorViewEvent) {
        when (useCase) {
            is DoortoDoorViewEvent.OnSuccessList -> {
                dismissLoading()
                val dataItem = useCase.responseGetStatusCall.data
                if (dataItem?.isNotEmpty()!!)
                    initAdapter(dataItem)
                else {
                    showView(tvNotFoundStatusCall)
                    hideView(rvStatusCallEndUser)
                }
                viewModel.callGetAddressPrimary()
            }
            is DoortoDoorViewEvent.OnFailed -> {
                val throwable = useCase.throwable
//                showToastExt(showErrorMessageThrowable(throwable), requireContext())
            }
            is DoortoDoorViewEvent.OnFailedAddress -> {
                val throwable = useCase.throwable
//                showToastExt(showErrorMessageThrowable(throwable), requireContext())
                dismissLoading()
            }

            is DoortoDoorViewEvent.OnSuccessListAddress -> {
                dismissLoading()
                dataAddress = useCase.responseListAddress.data
                showView(llAddressStatusCall)
                tvItemNameAddressSelectAccept.text = dataAddress?.addressName
                tvItemAddressSelectAccept.text = dataAddress?.address
                tvItemPesonAddressAccept.text = dataAddress?.person
                tvItemPhoneAddressSelectAccept.text = dataAddress?.phone
            }
        }
    }

    private fun initAdapter(dataItem: List<DataItemGetStatusCall?>?) {
        showView(rvStatusCallEndUser)
        hideView(tvNotFoundStatusCall)
        val groupieAdapter = GroupAdapter<GroupieViewHolder>().apply {
            dataItem?.forEach {
                add(AdapterStatusCallEndUser(it!!, onClickItemAnyList))
            }
        }

        setRvAdapterVertikal(rvStatusCallEndUser, groupieAdapter)
    }
}
