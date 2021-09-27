package id.streetbox.live.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.streetbox.live.ui.tabmenuvisit.MenuInstagramHomeVisitFragment
import id.streetbox.live.ui.tabmenuvisit.MenuMerchantHomeVisitFragment

class AdapterViewPagerMenuVisit(val mutableListString: Array<String>, activity: AppCompatActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return mutableListString.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                MenuMerchantHomeVisitFragment()
            }
            1 -> {
                MenuInstagramHomeVisitFragment()
            }
            else -> {
                MenuMerchantHomeVisitFragment()
            }
        }
    }

}