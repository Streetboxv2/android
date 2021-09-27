package com.zeepos.ui_base.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

/**
 * Created by Arif S. on 7/2/20
 */
class AutoFitGirdRecyclerView : RecyclerView {
    lateinit var gridLayoutManager: GridLayoutManager
    var columnWidth: Int = -1

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }


    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val attrsArray: IntArray = intArrayOf(android.R.attr.columnWidth)
            val array = context.obtainStyledAttributes(attrs, attrsArray)
            columnWidth = array.getDimensionPixelSize(0, -1)
            array.recycle()
        }

        gridLayoutManager = GridLayoutManager(context, 1)
        layoutManager = gridLayoutManager
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (columnWidth > 0) {
            val spanCount = max(1, measuredWidth / columnWidth)
            gridLayoutManager.spanCount = spanCount
        }
    }
}