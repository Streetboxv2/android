package id.streetbox.live.ui.termsconditions


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.setTextHTML
import id.streetbox.live.R
import kotlinx.android.synthetic.main.activity_term_condition.*
import javax.inject.Inject

class TermConditionActivity : BaseActivity<TermConditionViewEvent, TermConditionViewModel>() {

    @Inject
    lateinit var gson: Gson
    private lateinit var user: User

    override fun initResourceLayout(): Int {
        return R.layout.activity_term_condition
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(TermConditionViewModel::class.java)
        viewModel.callApiTermCondition()

        val bundle = intent.extras!!
        val userStr = bundle.getString("user")
        user = gson.fromJson(userStr, User::class.java)

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        showLoading()
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onEvent(useCase: TermConditionViewEvent) {
        when (useCase) {
            is TermConditionViewEvent.OnSuccessTermCondition -> {
                val dataTermCondition = useCase.responseTermCondition.data
                if (dataTermCondition != null) {
                    tvTermCondition.text = setTextHTML(dataTermCondition.value.toString())
                }
            }
            is TermConditionViewEvent.OnFailedTermCondition -> {
            }
        }
        dismissLoading()
    }

    companion object {
        private const val RC_IMAGE = 1
        fun getIntent(context: Context, bundle: Bundle): Intent {
            val intent = Intent(context, TermConditionActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }
    }
}