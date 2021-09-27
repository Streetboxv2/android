package id.streetbox.live.ui.main.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chad.library.adapter.base.listener.InstanceFragment
import com.zeepos.map.ui.MapActivity
import com.zeepos.models.ConstVar
import id.streetbox.live.R
import com.zeepos.ui_base.ui.BaseFragment
import id.streetbox.live.ui.main.doortodoor.DoortoDoorFragment
import id.streetbox.live.ui.main.home.homevisit.HomeVisitFragment
import id.streetbox.live.ui.main.home.nearby.NearByFragment
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by Arif S. on 6/13/20
 */
class HomeFragment : BaseFragment<HomeViewEvent, HomeViewModel>() {

    private var isSearchShow = false
    private lateinit var viewPagerAdapter: HomePagerAdapter

    val instanceFragment = object : InstanceFragment {
        override fun instanceFragment(position: Int): Fragment =
            when (position) {
                0 -> NearByFragment.newInstance()
                1 -> HomeVisitFragment.newInstance()
//                2 -> DoortoDoorFragment.newInstance("", "")
                else -> NearByFragment.newInstance()
            }
    }

    override fun initResourceLayout(): Int {
        return R.layout.fragment_home
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(HomeViewModel::class.java)
        viewPagerAdapter = HomePagerAdapter(childFragmentManager, instanceFragment)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        val menuItem = toolbar.menu.findItem(R.id.nv_search)

        viewpager.adapter = viewPagerAdapter
        tab.setupWithViewPager(viewpager)

        println("respon Tipe Notif $typeNotif")
        if (typeNotif.equals("listnotif")) {
            viewpager.currentItem = 2
        }

        toolbar.setNavigationOnClickListener {
            context?.let {
                startActivity(MapActivity.getIntent(it, type = ConstVar.MAP_TYPE_NEAR_BY))
            }
        }

        toolbar.setOnMenuItemClickListener {
            activity?.invalidateOptionsMenu()

            if (isSearchShow) {
                sv_search.isIconified = true
                sv_search.visibility = View.GONE
                iv_logo_top.visibility = View.VISIBLE
                isSearchShow = false

                context?.let {
                    menuItem.setIcon(ContextCompat.getDrawable(it, R.drawable.ic_search_white))
                }

            } else {
                sv_search.isIconified = false
                sv_search.visibility = View.VISIBLE
                iv_logo_top.visibility = View.GONE
                isSearchShow = true

                context?.let {
                    menuItem.setIcon(ContextCompat.getDrawable(it, R.drawable.ic_close))
                }
            }
            true
        }
    }

    override fun onEvent(useCase: HomeViewEvent) {
    }

    companion object {
        var typeNotif: String? = null
        fun newInstance(tipeNotif: String): HomeFragment {
            typeNotif = tipeNotif
            return HomeFragment()
        }
    }
}