package om.zeepos.map.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.zeepos.map.R
import com.zeepos.map.ui.CheckInListener
import com.zeepos.ui_base.ui.BaseDialogLoadingFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CheckInDialog : BaseDialogLoadingFragment(), View.OnClickListener {
    private var mainDialog: Dialog? = null
    private var isClickCancelAble = true
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
        var address = mArgs!!.getString("address")
        var startdate = mArgs!!.getString("start_date")
        var enddate = mArgs!!.getString("end_date")

//        mainDialog?.window?.decorView?.let { view ->
////
//            txt_address = view.findViewById<View>(R.id.txt_address) as TextView
//            if(address==null){address=""}
//            txt_address!!.setText(address)
//            txt_startdate = view.findViewById<View>(R.id.txt_startdate) as TextView
//            if(startdate==null){startdate=""}
//            txt_startdate!!.setText((convertToNewFormat(startdate)))
//            txt_enddate = view.findViewById<View>(R.id.txt_enddate) as TextView
//            if(enddate==null){enddate=""}
//            txt_enddate!!.setText((convertToNewFormat(enddate)))
//
//            val btnCheckIn = view.findViewById<Button>(R.id.bt_checkIn)
//            btnCheckIn.setOnClickListener(this)
//        }
        return mainDialog!!
    }




    companion object {
        fun newInstance(): CheckInDialog {
            return CheckInDialog()
        }
    }

    override fun onClick(v: View?) {
        val dialogListener = activity as CheckInListener
                    dialogListener.onFinishedCheckedIn()
                    dismiss()
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
