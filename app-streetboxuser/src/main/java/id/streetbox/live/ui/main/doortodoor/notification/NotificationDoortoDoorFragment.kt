package id.streetbox.live.ui.main.doortodoor.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.response.DataItemNotificationBlast
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.hideView
import com.zeepos.utilities.intentPageData
import com.zeepos.utilities.setRvAdapterVertikal
import com.zeepos.utilities.showView
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterListNotifBlast
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewEvent
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewModel
import id.streetbox.live.ui.onclick.OnClickItemAny
import kotlinx.android.synthetic.main.fragment_notification_doorto_door.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationDoortoDoorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationDoortoDoorFragment : BaseFragment<DoortoDoorViewEvent, DoortoDoorViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val onClickItemAny = object : OnClickItemAny {
        override fun clickItem(any: Any) {
            val dataItemNotificationBlast = any as DataItemNotificationBlast
            val intent =
                intentPageData(requireContext(), NotificationDoortoDoorMapsActivity::class.java)
                    .putExtra(ConstVar.DATA_ITEM_NOTIF, dataItemNotificationBlast)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationDoortoDoorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun initResourceLayout(): Int {
        return R.layout.fragment_notification_doorto_door
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(DoortoDoorViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.callGetListNotif()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        val user = viewModel.getUserLocal()
//        showLoading()

        val getSaveTopic = Hawk.get<String>("saveTopic")

        if (getSaveTopic != null) {
            if (getSaveTopic.equals("save"))
                switchNotif.isChecked = true
        } else {
            suscribeNotif(user?.id.toString())
            switchNotif.isChecked = true
        }


        switchNotif.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                suscribeNotif(user?.id.toString())
            } else unSuscribeNotif(user?.id.toString())
        }

    }

    fun suscribeNotif(userId: String) {
        showLoading()
        Hawk.put("saveTopic", "save")
        FirebaseMessaging.getInstance().subscribeToTopic("blast_$userId")
            .addOnSuccessListener {
                dismissLoading()
            }
    }

    fun unSuscribeNotif(userId: String) {
        showLoading()
        Hawk.delete("saveTopic")
        FirebaseMessaging.getInstance().unsubscribeFromTopic("blast_$userId")
            .addOnSuccessListener {
                dismissLoading()
            }
    }

    override fun onEvent(useCase: DoortoDoorViewEvent) {
        when (useCase) {
            is DoortoDoorViewEvent.OnSuccessListNotif -> {
                dismissLoading()
                val dataItem = useCase.responseListNotificationBlast.data
                if (dataItem!!.isNotEmpty())
                    initAdapter(dataItem)
                else {
                    showView(tvNotFoundNotif)
                    hideView(rvListNotifBlast)
                }
            }
            is DoortoDoorViewEvent.OnFailed -> {
                dismissLoading()
            }
        }
    }

    private fun initAdapter(dataItem: List<DataItemNotificationBlast?>) {
        hideView(tvNotFoundNotif)
        showView(rvListNotifBlast)
        val groupieAdapter = GroupAdapter<GroupieViewHolder>().apply {
            dataItem.sortedByDescending { it?.createdAt }.forEach {
                add(AdapterListNotifBlast(it!!, onClickItemAny))
            }
        }

        setRvAdapterVertikal(rvListNotifBlast, groupieAdapter)
    }
}