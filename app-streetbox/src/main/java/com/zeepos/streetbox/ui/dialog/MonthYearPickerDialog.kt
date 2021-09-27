package com.zeepos.streetbox.ui.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.zeepos.streetbox.R
import java.util.*

/**
 * Created by Arif S. on 8/24/20
 */
class MonthYearPickerDialog : DialogFragment() {
    private val MAX_YEAR = 2099
    private var listener: DatePickerDialog.OnDateSetListener? = null

    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        // Get the layout inflater
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val cal: Calendar = Calendar.getInstance()
        val dialog: View = inflater.inflate(R.layout.date_picker_dialog, null)
        val monthPicker: NumberPicker = dialog.findViewById(R.id.picker_month) as NumberPicker
        val yearPicker: NumberPicker = dialog.findViewById(R.id.picker_year) as NumberPicker
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal.get(Calendar.MONTH)
        val year: Int = cal.get(Calendar.YEAR)
        yearPicker.minValue = year
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year
        builder.setView(dialog) // Add action buttons
            .setPositiveButton(
                "OK"
            ) { p0, p1 -> listener?.onDateSet(null, yearPicker.value, monthPicker.value, 0) }
            .setNegativeButton(
                R.string.cancel
            ) { p0, p1 -> this@MonthYearPickerDialog.dialog!!.cancel() }
        return builder.create()
    }
}