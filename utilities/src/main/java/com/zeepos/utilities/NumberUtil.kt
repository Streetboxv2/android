package com.zeepos.utilities

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by Arif S. on 6/14/20
 */
object NumberUtil {
    private const val FORMAT_NO_DECIMAL = "###,###"

    private const val FORMAT_WITH_DECIMAL = "###,###.###"

    private fun getCharOccurrence(input: String, c: Char): Int {
        var occurrence = 0
        val chars = input.toCharArray()
        for (thisChar in chars) {
            if (thisChar == c) {
                occurrence++
            }
        }
        return occurrence
    }

    fun getCommaOccurrence(input: String): Int {
        return getCharOccurrence(input, ',')
    }

    fun extractDigits(input: String): String {
        return input.replace("\\D+".toRegex(), "")
    }

    fun formatToStringWithoutDecimal(value: Double): String {
        val formatter = DecimalFormat(FORMAT_NO_DECIMAL)
        formatter.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        val result = formatter.format(value)
        return result.replace(',', '.')
    }

    fun formatToStringWithoutDecimal(value: Int): String {
        val formatter = DecimalFormat(FORMAT_NO_DECIMAL)
        formatter.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        val result = formatter.format(value)
        return result.replace(',', '.')
    }

    fun formatToStringWithoutDecimal(value: String): String {
        return formatToStringWithoutDecimal(value.toDouble())
    }

    fun formatWithDecimal(price: String): String {
        return formatWithDecimal(price.toDouble())
    }

    private fun formatWithDecimal(price: Double): String {
        val formatter = DecimalFormat(FORMAT_WITH_DECIMAL)
        formatter.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        return formatter.format(price)
    }
}