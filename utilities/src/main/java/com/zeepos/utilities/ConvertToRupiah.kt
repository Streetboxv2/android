package com.zeepos.utilities

import android.text.TextUtils
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class ConvertToRupiah {
    companion object {
        fun toRupiah(currency: String?, price: String, diskon: Boolean): String? {
            var price = price
            if (TextUtils.isEmpty(price)) {
                price = "0"
            }
            val kursIndonesia =
                DecimalFormat.getCurrencyInstance(Locale.getDefault()) as DecimalFormat
            //format.setCurrency(Currency.getInstance(currency));
            val formatRp = DecimalFormatSymbols()
            formatRp.currencySymbol = currency
            formatRp.monetaryDecimalSeparator = ','
            formatRp.groupingSeparator = '.'
            kursIndonesia.minimumFractionDigits = 0
            kursIndonesia.decimalFormatSymbols = formatRp
            return if (diskon) kursIndonesia.format(price.toDouble() * 2) else kursIndonesia.format(
                price.toDouble()
            )
        }
    }
}