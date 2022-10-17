package io.joelt.texttemplate.ui.views.breakable

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import io.joelt.texttemplate.ui.views.ViewPosition
import io.joelt.texttemplate.ui.views.ViewsLayout
import kotlin.math.max

private fun TextView.copyTo(view: TextView) {
    view.text = this.text
    view.setTextColor(this.currentTextColor)
    view.background = this.background

    if (this.layoutParams != null) {
        view.layoutParams = this.layoutParams
    }
}

class BreakableTextView : Breakable {
    lateinit var rootTextView: TextView
    // The TextView on the same line as the offset
    private lateinit var tv1: TextView
    // The TextView on next and subsequent lines
    private lateinit var tv2: TextView
    private var layoutCache: ViewsLayout = ViewsLayout()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        tv1 = TextView(context)
        tv2 = TextView(context)

        addView(tv1)
        addView(tv2)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)

        // Copy the attributes to the children text views
        rootTextView.copyTo(tv1)
        rootTextView.copyTo(tv2)

        if (specWidthMode == MeasureSpec.UNSPECIFIED) {
            // Display the view normally
            tv1.measure(widthMeasureSpec, heightMeasureSpec)
            layoutCache.setViewAt(0, ViewPosition(leftOffset, 0))
            layoutCache.measuredWidth = tv1.measuredWidth + leftOffset
            layoutCache.measuredHeight = tv1.measuredHeight

            tv2.visibility = View.GONE
            setMeasuredOffsets(
                ViewPosition(leftOffset + tv1.measuredWidth, 0),
                max(rowMaxHeight, tv1.measuredHeight)
            )
            setMeasuredDimension(
                layoutCache.measuredWidth,
                layoutCache.measuredHeight
            )
            return
        }

        // Find out how big the view wants to be
        tv1.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        )

        val widthLeft = specWidth - leftOffset
        if (tv1.measuredWidth <= widthLeft) {
            // The view can fit within the space, display normally
            tv1.measure(
                MeasureSpec.makeMeasureSpec(widthLeft, specWidthMode),
                heightMeasureSpec
            )

            layoutCache.setViewAt(0, ViewPosition(leftOffset, 0))
            layoutCache.measuredWidth = tv1.measuredWidth + leftOffset
            layoutCache.measuredHeight = tv1.measuredHeight

            tv2.visibility = View.GONE
            setMeasuredOffsets(
                ViewPosition(leftOffset + tv1.measuredWidth, 0),
                max(rowMaxHeight, tv1.measuredHeight)
            )
            setMeasuredDimension(
                layoutCache.measuredWidth,
                layoutCache.measuredHeight
            )
            return
        }

        // The text is going to overflow, see where to break the text
        val strIndex = breakText(tv1, widthLeft)
        tv1.text = tv1.text.subSequence(0, strIndex)
        tv2.text = tv2.text.subSequence(strIndex, tv2.text.length)
        tv2.visibility = View.VISIBLE

        if (specHeightMode == MeasureSpec.UNSPECIFIED) {
            tv1.measure(
                MeasureSpec.makeMeasureSpec(widthLeft, specWidthMode),
                heightMeasureSpec
            )
        } else {
            tv1.measure(
                MeasureSpec.makeMeasureSpec(widthLeft, specWidthMode),
                MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.AT_MOST)
            )
        }
        layoutCache.setViewAt(0, ViewPosition(leftOffset, 0))

            rowMaxHeight = max(rowMaxHeight, tv1.measuredHeight)
        // Move to the next row
        if (specHeightMode == MeasureSpec.UNSPECIFIED) {
            tv2.measure(
                widthMeasureSpec,
                heightMeasureSpec
            )
        } else {
            val heightLeft = max(0, specHeight - tv1.measuredHeight)
            tv2.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(heightLeft, specHeightMode)
            )
        }
        layoutCache.setViewAt(1, ViewPosition(0, rowMaxHeight))
        layoutCache.measuredWidth = max(
            leftOffset + tv1.measuredWidth,
            tv2.measuredWidth
        )
        layoutCache.measuredHeight = rowMaxHeight + tv2.measuredHeight

        // Calculate measuredEnd
        val lastLine = tv2.lineCount - 1
        val layout = tv2.layout
        val lastLineWidth = layout.getLineWidth(lastLine).toInt()
        val lastLineTop = layout.getLineTop(lastLine) + layout.topPadding
        setMeasuredOffsets(
            ViewPosition(lastLineWidth, rowMaxHeight + lastLineTop),
            tv2.measuredHeight - lastLineTop
        )
        setMeasuredDimension(
            layoutCache.measuredWidth,
            layoutCache.measuredHeight
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!changed) {
            return
        }

        children.filter {
            it.visibility != View.GONE
        }.forEachIndexed { index, view ->
            val position = layoutCache.viewAt(index)!!
            view.layout(
                position.left,
                position.top,
                position.left + view.measuredWidth,
                position.top + view.measuredHeight
            )
        }
    }
}
