package com.zeepos.ui_base.views

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.zeepos.ui_base.R

/**
 * Created by Arif S. on 5/11/20
 */
object TypefaceHelper {
    private var typefaceRegular: Typeface? = null
    private var typefaceBold: Typeface? = null
    private var typefaceMedium: Typeface? = null
    private var typefaceBlack: Typeface? = null
    private var typefaceThin: Typeface? = null
    private var typefaceItalic: Typeface? = null

    fun init(context: Context) {
        typefaceRegular = ResourcesCompat.getFont(context, R.font.poppins_regular)
        typefaceItalic = ResourcesCompat.getFont(context, R.font.poppins_italic)
        typefaceBold = ResourcesCompat.getFont(context, R.font.poppins_bold)
        typefaceMedium = ResourcesCompat.getFont(context, R.font.poppins_medium)
        typefaceBlack = ResourcesCompat.getFont(context, R.font.poppins_black)
        typefaceThin = ResourcesCompat.getFont(context, R.font.poppins_thin)
    }

    fun setRegular(textView: TextView?) {
        if (typefaceRegular != null)
            textView?.typeface = typefaceRegular
    }

    fun setItalic(textView: TextView?) {
        if (typefaceItalic != null)
            textView?.typeface = typefaceItalic
    }

    fun setBold(textView: TextView?) {
        if (typefaceBold != null)
            textView?.typeface = typefaceBold
    }

    fun setMedium(textView: TextView?) {
        if (typefaceMedium != null)
            textView?.typeface = typefaceMedium
    }

    fun setBlack(textView: TextView?) {
        if (typefaceBlack != null)
            textView?.typeface = typefaceBlack
    }

    fun setThin(textView: TextView?) {
        if (typefaceThin != null)
            textView?.typeface = typefaceThin
    }

}