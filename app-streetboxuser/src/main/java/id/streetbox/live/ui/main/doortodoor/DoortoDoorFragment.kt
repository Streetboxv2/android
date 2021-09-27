package id.streetbox.live.ui.main.doortodoor

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.listener.InstanceFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.TrackGps
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterViewPagerDoortoDoor
import id.streetbox.live.ui.main.doortodoor.mycall.MyCallFragment
import id.streetbox.live.ui.main.doortodoor.notification.NotificationDoortoDoorFragment
import kotlinx.android.synthetic.main.fragment_doorto_door.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoortoDoorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoortoDoorFragment : BaseFragment<DoortoDoorViewEvent, DoortoDoorViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var adapterViewPagerDoortoDoor: AdapterViewPagerDoortoDoor? = null

    private val titles =
        arrayOf("Available To Call", "Call Activities")

    val instanceFragment = object : InstanceFragment {
        override fun instanceFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    NotificationDoortoDoorFragment()
                }
                1 -> {
                    MyCallFragment()
                }
                else -> {
                    MyCallFragment()
                }
            }
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
        var typeNotif: String? = null

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoortoDoorFragment().apply {
                typeNotif = param1
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun initResourceLayout(): Int {
        return R.layout.fragment_doorto_door
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(DoortoDoorViewModel::class.java)
    }


    override fun onViewReady(savedInstanceState: Bundle?) {
        setTabViewPager()
    }

    override fun onEvent(useCase: DoortoDoorViewEvent) {

    }

    private fun setTabViewPager() {
        adapterViewPagerDoortoDoor = AdapterViewPagerDoortoDoor(titles, this, instanceFragment)
        viewPagerDoortoDoor.adapter = adapterViewPagerDoortoDoor
        TabLayoutMediator(
            tabDoortoDoor, viewPagerDoortoDoor
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = titles[position]
        }.attach()

        viewPagerDoortoDoor.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                if (typeNotif.equals("enduser")) {
                    viewPagerDoortoDoor.currentItem = position + 1
                }
                super.onPageSelected(position)
            }
        })


    }

}