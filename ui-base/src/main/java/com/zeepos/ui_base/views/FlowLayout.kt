package com.zeepos.ui_base.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by Arif S. on 8/9/20
 */
class FlowLayout : ViewGroup {
    constructor(context: Context) :
            super(context)

    constructor(context: Context, attrs: AttributeSet) :
            super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {

    }
}