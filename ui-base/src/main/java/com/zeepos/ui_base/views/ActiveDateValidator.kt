package com.zeepos.ui_base.views

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import com.zeepos.utilities.DateTimeUtil
import java.util.*

/**
 * Created by Arif S. on 6/22/20
 */
class ActiveDateValidator(
    private val activeDate: List<Long> = arrayListOf()
) : CalendarConstraints.DateValidator {

    private val utc: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    private val currentTime = DateTimeUtil.getCurrentDateWithoutTime(utc.timeInMillis)

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
    }

    override fun isValid(date: Long): Boolean {
//        utc.timeInMillis = date
//        val dayOfWeek = utc[Calendar.DAY_OF_WEEK]
//        val dayOfMonth = utc[Calendar.DAY_OF_MONTH]
//        return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY
        return (date >= currentTime) && activeDate.contains(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is ActiveDateValidator) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val hashedFields = arrayOf<Any>()
        return hashedFields.contentHashCode()
    }

    companion object CREATOR : Parcelable.Creator<ActiveDateValidator> {
        override fun createFromParcel(parcel: Parcel): ActiveDateValidator {
            return ActiveDateValidator(parcel)
        }

        override fun newArray(size: Int): Array<ActiveDateValidator?> {
            return arrayOfNulls(size)
        }
    }
}