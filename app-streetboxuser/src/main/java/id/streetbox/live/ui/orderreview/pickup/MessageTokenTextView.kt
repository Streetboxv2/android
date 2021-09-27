package id.streetbox.live.ui.orderreview.pickup

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tokenautocomplete.TokenCompleteTextView
import id.streetbox.live.R

/**
 * Created by Arif S. on 7/25/20
 */
class MessageTokenTextView : TokenCompleteTextView<String> {

    constructor(context: Context) :
            super(context)

    constructor(context: Context, attrs: AttributeSet) :
            super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun getViewForObject(message: String?): View {
        val l =
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val token: TextView = l.inflate(
            R.layout.item_template_msg,
            parent as ViewGroup,
            false
        ) as TextView

        token.text = message
        return token
    }

    override fun defaultObject(completionText: String?): String {
        return completionText ?: ""
    }
}