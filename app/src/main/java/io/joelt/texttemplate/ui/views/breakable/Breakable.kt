package io.joelt.texttemplate.ui.views.breakable

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import io.joelt.texttemplate.ui.views.ViewPosition

abstract class Breakable : ViewGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var measuredEnd = ViewPosition(0, 0)
    var rowMaxHeight = 0
    protected var offset = ViewPosition(0, 0)

    fun measure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        offset: ViewPosition,
        rowMaxHeight: Int
    ) {
        this.offset = offset
        this.rowMaxHeight = rowMaxHeight
        measure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setMeasuredOffsets(end: ViewPosition, maxHeight: Int) {
        measuredEnd = end
        rowMaxHeight = maxHeight
    }
}
