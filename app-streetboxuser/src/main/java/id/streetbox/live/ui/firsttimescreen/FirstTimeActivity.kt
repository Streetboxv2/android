package id.streetbox.live.ui.firsttimescreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import id.streetbox.live.R
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_base.views.SlidingDrawableImageAdapter
import com.zeepos.ui_login.LoginActivity
import kotlinx.android.synthetic.main.activity_first_time.*

/**
 * Created by Arif S. on 8/4/20
 */
class FirstTimeActivity : BaseActivity<FirstTimeViewEvent, FirstTimeViewModel>() {
    override fun initResourceLayout(): Int {
        return R.layout.activity_first_time
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(FirstTimeViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        val images: MutableList<Int> = mutableListOf()
        images.add(R.drawable.img_step_1)
        images.add(R.drawable.img_step_2)
        images.add(R.drawable.img_step_3)
        images.add(R.drawable.img_step_4)

        val slideAdapter = SlidingDrawableImageAdapter(this, images, itemChildClick)

        viewpager.adapter = slideAdapter
        viewpager.removeScale()
        vpd_indicator.setViewPager(viewpager)
    }

    override fun onEvent(useCase: FirstTimeViewEvent) {

    }

    private val itemChildClick = object : SlidingDrawableImageAdapter.ItemChildClick {
        override fun onNextClick(currentPos: Int) {
            viewpager.currentItem = currentPos + 1
        }

        override fun onFinished() {
            startActivity(LoginActivity.getIntent(this@FirstTimeActivity))
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, FirstTimeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }
}