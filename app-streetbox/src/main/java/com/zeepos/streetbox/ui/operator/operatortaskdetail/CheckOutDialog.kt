package com.zeepos.streetbox.ui.operator.operatortaskdetail

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.zeepos.models.master.ParkingSpace
import com.zeepos.streetbox.R
import com.zeepos.ui_base.ui.BaseDialogLoadingFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CheckOutDialog : BaseDialogLoadingFragment(), View.OnClickListener {
    lateinit var parkingSpace: ParkingSpace
    private var isBooked: Boolean = false
    private var mainDialog: Dialog? = null
    private var isClickCancelAble = true
    private  var btnCheckout: Button? = null
    private var txt_startdate: TextView? = null
    private var txt_enddate:TextView? = null
    private var txt_address:TextView? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mainDialog = Dialog(requireActivity(), R.style.Theme_MaterialComponents_BottomSheetDialog).apply {
            setContentView(R.layout.dialog_foodtruck_checkin)
            setCanceledOnTouchOutside(isClickCancelAble)
            window?.setGravity(Gravity.CENTER)
        }
            val mArgs = arguments
            val address = mArgs!!.getString("address")
            val startdate = mArgs!!.getString("start_date")
            val enddate = mArgs!!.getString("end_date")

        mainDialog?.window?.decorView?.let { view ->

            txt_address = view.findViewById<View>(R.id.txt_address) as TextView
            txt_address!!.setText(address)
            txt_startdate = view.findViewById<View>(R.id.txt_startdate) as TextView
            txt_startdate!!.setText((startdate))
            txt_enddate = view.findViewById<View>(R.id.txt_enddate) as TextView
            txt_enddate!!.setText((enddate))
            btnCheckout = view.findViewById<View>(R.id.btn_checkOut) as Button
            btnCheckout?.setOnClickListener {

                val dialogListener = activity as OperatorListener
                dialogListener.onFinishedCheckOut()
                dismiss()
            }
        }
        return mainDialog!!
    }


    companion object {
        fun newInstance(): CheckOutDialog {
            return CheckOutDialog()
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    @Throws(ParseException::class)
    fun convertToNewFormat(dateStr: String?): String? {
        val utc = TimeZone.getTimeZone("UTC")
        val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'")
        val destFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        sourceFormat.setTimeZone(utc)
        val convertedDate: Date = sourceFormat.parse(dateStr)
        return destFormat.format(convertedDate)
    }


}
