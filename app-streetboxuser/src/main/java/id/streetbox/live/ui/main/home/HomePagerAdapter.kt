package id.streetbox.live.ui.main.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.chad.library.adapter.base.listener.InstanceFragment
import id.streetbox.live.ui.main.doortodoor.DoortoDoorFragment
import id.streetbox.live.ui.main.home.homevisit.HomeVisitFragment
import id.streetbox.live.ui.main.home.nearby.NearByFragment

/**
 * Created by Arif S. on 6/14/20
 */
class HomePagerAdapter(fm: FragmentManager, val instanceFragment: InstanceFragment) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = instanceFragment.instanceFragment(position)

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Nearby"
            1 -> return "Home Visit"
//            2 -> return "Door To Door"
        }

        return "Nearby"
    }

    override fun getCount(): Int = 2
}