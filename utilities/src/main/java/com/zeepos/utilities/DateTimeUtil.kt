package com.zeepos.utilities

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Arif S. on 5/11/20
 */
object DateTimeUtil {
    const val FORMAT_DATE_WITH_TIME = "dd/MM/yyyy HH:mm"
    const val FORMAT_DATE_WITHOUT_TIME = "dd/MM/yyyy"
    const val FORMAT_DAY_DATE_WITHOUT_TIME = "EEEE yyyy/MM/dd"
    const val FORMAT_DATE_ONLY = "dd"
    const val FORMAT_DATE_SERVER = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val FORMAT_DATE_SERVER_2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
//    private val timeZone = TimeZone.getTimeZone("UTC")
    val timeZone = TimeZone.getTimeZone("GMT")

    fun getCurrentDateTime(): Long {
        val calendar: Calendar = Calendar.getInstance(timeZone)
//        val calendar: Calendar = Calendar.getInstance()
        return calendar.time.time
    }

    fun getCurrentLocalDateTime(): Long {
        val calendar: Calendar = Calendar.getInstance()
        return calendar.time.time
    }

    fun getCurrentLocalDateWithoutTime(): Long {
        val calendar: Calendar = Calendar.getInstance(timeZone)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time.time
    }

    fun getCurrentDateWithoutTime(): Long {
        val calendar: Calendar = Calendar.getInstance(timeZone)
//        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time.time
    }

    fun getCurrentDateWithoutTime(timeMillis: Long): Long {
        val calendar: Calendar = Calendar.getInstance(timeZone)
//        val calendar: Calendar = Calendar.getInstance()
        calendar.time = Date(timeMillis)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time.time
    }

    fun getCurrentDateWithoutSecond(timeMillis: Long): Long {
        val calendar: Calendar = Calendar.getInstance(timeZone)
//        val calendar: Calendar = Calendar.getInstance()
        calendar.time = Date(timeMillis)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time.time
    }

    fun getPreviousDate(currentTime: Long): Long {
        val calendar: Calendar = Calendar.getInstance(timeZone)
//        val calendar: Calendar = Calendar.getInstance()
        calendar.time = Date(currentTime)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return calendar.time.time
    }

    fun getNextDate(currentTime: Long): Long {
        val calendar: Calendar = Calendar.getInstance(timeZone)
//        val calendar: Calendar = Calendar.getInstance()
        calendar.time = Date(currentTime)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.time.time
    }

    fun getDay(currentTime: Long): Int {
        val calendar: Calendar = Calendar.getInstance(timeZone)
//        val calendar: Calendar = Calendar.getInstance()
        calendar.time = Date(currentTime)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getDateWithFormat(timeMillis: Long, format: String): String {
        val sdf: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = timeZone
        return sdf.format(timeMillis)
    }

    fun getLocalDateWithFormat(timeMillis: Long, format: String): String {
        val sdf: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = timeZone
        return sdf.format(timeMillis)
    }

    fun getDateWithFormat(dateStr: String, format: String): String {
        val timeMillis = getDateFromString(dateStr, FORMAT_DATE_SERVER_2)?.time ?: 0
        return getDateWithFormat(timeMillis, format)
    }

    fun getDateWithFormat1(dateStr: String, format: String): String {
        val sourceFormat = SimpleDateFormat(FORMAT_DATE_SERVER)
        val destFormat = SimpleDateFormat(format)
        val convertedDate: Date = sourceFormat.parse(dateStr)
        return destFormat.format(convertedDate)
    }

    fun getCurrentTimeStamp(format: String): String? {
        return SimpleDateFormat(format).format(Date())
    }


    fun getDateFromString(dateStr: String?, format: String = FORMAT_DATE_SERVER): Date? {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = timeZone

        if (dateStr != null && dateStr.isNotEmpty()) {
            try {
                return sdf.parse(dateStr)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return null
    }

}