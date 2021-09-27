package com.zeepos.streetbox.ui.operator.operatortask

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.streetbox.R
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.SharedPreferenceUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class OperatorTaskAdapter(data: MutableList<TaskOperator> = arrayListOf()) :
    BaseQuickAdapter<TaskOperator, BaseViewHolder>(R.layout.operator_task_item, data) {
    init {
        addChildClickViewIds(R.id.btn_checkout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun convert(holder: BaseViewHolder, item: TaskOperator) {
        val btnCheckout = holder.getView<Button>(R.id.btn_checkout)
        val dateSchedule = DateTimeUtil.getDateWithFormat1(item.scheduleDate!!,"EEEE dd-MM-yyyy") + " " + DateTimeUtil.getDateWithFormat1(item.startDate!!,"HH:mm")+" - " + DateTimeUtil.getDateWithFormat1(item.endDate!!,"HH:mm")
        holder.setText(R.id.tv_title, item.name)
        holder.setText(R.id.tv_address, item.address)
        holder.setText(R.id.tv_startdatetime, dateSchedule)
        if(item.types == "HOMEVISIT"){
//            holder.getView<TextView>(R.id.tv_termncondition).visibility = View.VISIBLE
//            holder.getView<Switch>(R.id.switchButton).visibility = View.VISIBLE
            holder.getView<TextView>(R.id.home_visit).visibility = View.VISIBLE
        }

        DateTimeUtil.getDateWithFormat1(item.scheduleDate!!,"dd-MM-yyyy")?.let { checkDataSize(it) }
        if(item.status == 3){

            btnCheckout.visibility = View.VISIBLE
        }else{
            btnCheckout.visibility = View.GONE
        }
    }


    @SuppressLint("NewApi")
    fun checkDataSize(startDate:String){
        if(startDate.equals(DateTimeUtil.getCurrentDateTime()))
            context?.let {
                SharedPreferenceUtil.setString(
                    it,
                    "date",
                    startDate
                )
            }
    }


}