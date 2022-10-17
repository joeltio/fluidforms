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

class BreakableTextView : Breakable {
    private var rootTextView: TextView
    // The TextView on the same line as the offset
    private var tv1: TextView
    // The TextView on next and subsequent lines
    private var tv2: TextView
    private var layoutCache: ViewsLayout = ViewsLayout()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        rootTextView = TextView(context)
        tv1 = TextView(context)
        tv2 = TextView(context)

        addView(tv1)
        addView(tv2)
    }

    // TextView properties
    var text: CharSequence
    get() {
        return rootTextView.text
    }
    set(value) {
        if (value != rootTextView.text) {
            rootTextView.text = value
            requestLayout()
        }
    }

    fun setTextColor(color: Int) {
        rootTextView.setTextColor(color)
        tv1.setTextColor(color)
        tv2.setTextColor(color)
    }

    override fun setBackgroundColor(color: Int) {
        rootTextView.setBackgroundColor(color)
        tv1.setBackgroundColor(color)
        tv2.setBackgroundColor(color)
    }

    // onMeasure and onLayout
    private fun positionTv1Only() {
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
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)

        // Copy elements that will change the layout of the view to tv1 and tv2
        tv1.text = rootTextView.text
        tv2.text = rootTextView.text

        // Now checking for two cases:
        // 1. Width is unspecified
        // 2. Width left is sufficient for the text view
        tv1.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            heightMeasureSpec
        )

        val widthLeft = specWidth - leftOffset
        if (specWidthMode == MeasureSpec.UNSPECIFIED
            || tv1.measuredWidth <= widthLeft) {
            positionTv1Only()
            return
        }

        // Now dealing with the third case:
        // 3. Width left is insufficient for the text view
        // Break the text into the two text views
        val strIndex = breakText(tv1, widthLeft)
        tv1.text = tv1.text.subSequence(0, strIndex)
        tv2.text = tv2.text.subSequence(strIndex, tv2.text.length)
        tv2.visibility = View.VISIBLE

        // Remeasure tv1 with the new text with at most the spec's height
        val tv1HeightMode = if (specHeightMode != MeasureSpec.EXACTLY) {
            specHeightMode
        } else { MeasureSpec.AT_MOST }
        tv1.measure(
            MeasureSpec.makeMeasureSpec(widthLeft, specWidthMode),
            tv1HeightMode
        )

        // Position tv1
        layoutCache.setViewAt(0, ViewPosition(leftOffset, 0))

        // Measure tv2 with the leftover height
        rowMaxHeight = max(rowMaxHeight, tv1.measuredHeight)
        val tv2Height = if (specHeightMode != MeasureSpec.UNSPECIFIED) {
            max(0, specHeight - rowMaxHeight)
        } else { 0 }
        tv2.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(tv2Height, specHeightMode)
        )

        // Position tv2
        layoutCache.setViewAt(1, ViewPosition(0, rowMaxHeight))

        layoutCache.measuredWidth = max(
            leftOffset + tv1.measuredWidth,
            tv2.measuredWidth
        )
        layoutCache.measuredHeight = rowMaxHeight + tv2.measuredHeight
        setMeasuredDimension(
            layoutCache.measuredWidth,
            layoutCache.measuredHeight
        )

        // Calculate measuredEnd
        val lastLine = tv2.lineCount - 1
        val layout = tv2.layout
        val lastLineWidth = layout.getLineWidth(lastLine).toInt()
        val lastLineTop = layout.getLineTop(lastLine) + layout.topPadding
        setMeasuredOffsets(
            ViewPosition(lastLineWidth, rowMaxHeight + lastLineTop),
            tv2.measuredHeight - lastLineTop
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
