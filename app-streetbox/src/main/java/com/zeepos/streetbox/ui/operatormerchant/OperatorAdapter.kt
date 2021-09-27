package com.zeepos.streetbox.ui.operatormerchant

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import com.zeepos.streetbox.R

/**
 * Created by Arif S. on 5/16/20
 */
class OperatorAdapter(data: MutableList<User> = arrayListOf()) :
    BaseQuickAdapter<User, BaseViewHolder>(R.layout.operator_item, data) {

    var isAssignEnabled: Boolean = false
    var isLiveTracking: Boolean = false

    init {
        addChildClickViewIds(R.id.tv_assign, R.id.btn_tracking)
    }

    override fun convert(holder: BaseViewHolder, item: User) {
        val tvAssign = holder.getView<TextView>(R.id.tv_assign)
        val btnTracking = holder.getView<MaterialButton>(R.id.btn_tracking)

        btnTracking.text = "Tracking"

        val name = if (item.platNo != null && item.platNo!!.isNotEmpty())
            item.platNo
        else item.name

        holder.setText(R.id.tv_name, name)

        when {
            isAssignEnabled -> {
                tvAssign.visibility = View.VISIBLE
                btnTracking.visibility = View.GONE
            }
            isLiveTracking -> {
                if (item.tasksId > 0) {
                    when (item.status) {
                        ConstVar.TASK_STATUS_ARRIVED -> {
                            btnTracking.text = "Show"
                            btnTracking.visibility = View.VISIBLE
                            tvAssign.visibility = View.INVISIBLE
                        }
                        ConstVar.TASK_STATUS_IN_PROGRESS -> {
                            btnTracking.visibility = View.VISIBLE
                            tvAssign.visibility = View.INVISIBLE
                        }
                        else -> {
                            btnTracking.visibility = View.INVISIBLE
                            tvAssign.visibility = View.INVISIBLE
                        }
                    }

                } else {
                    btnTracking.visibility = View.INVISIBLE
                    tvAssign.visibility = View.INVISIBLE
                }
            }
            else -> {
                btnTracking.visibility = View.GONE
                tvAssign.visibility = View.INVISIBLE
            }
        }

    }
}