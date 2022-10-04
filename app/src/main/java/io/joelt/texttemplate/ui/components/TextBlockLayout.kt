package io.joelt.texttemplate.ui.components

import android.content.Context
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.children
import kotlin.math.max
import kotlin.math.min

private fun TextView.makeSimpleCopy(): TextView {
    val tv = TextView(this.context)
    tv.text = this.text
    tv.layoutParams = this.layoutParams
    return tv
}

class TextBlockLayout : ViewGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        // TODO
    }

    private fun getTextBreak(text: CharSequence, paint: TextPaint, availableSpace: Float): Int {
        // TextUtils uses "\u2026" as the normal ellipsis. Measure that and add it in
        // the available space
        // See: https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/text/TextUtils.java#138
        val ellipsisLen = paint.measureText("\u2026")
        // Note that the ellipsized text has one extra character for the ellipsis
        // ellipsize ellipsizes "hello world" as either:
        // ""        - an empty string: not enough space for a single character of the original string
        // "h..."    - len 2 string: enough space for only one character
        // "hell..." - len n string: ellipsizes normally
        val ellipsized = TextUtils.ellipsize(text, paint, availableSpace + ellipsisLen, TextUtils.TruncateAt.END)

        // Return the index of the start of the other half
        // ellipsized may be of length 0. This means that there is no room for
        return max(ellipsized.length - 1, 0)
    }

    private fun measureHeight(children: Sequence<View>, maxWidth: Int, maxHeight: Int): Int {
        var width = 0
        var height = 0
        var rowMaxHeight = 0

        val virtualChildren = ArrayDeque(children.toMutableList())
        while (virtualChildren.isNotEmpty()) {
            val child = virtualChildren.first()

            rowMaxHeight = max(rowMaxHeight, child.measuredHeight)
            // Stack the view horizontally
            if ((width + child.measuredWidth) <= maxWidth) {
                width += child.measuredWidth
                virtualChildren.removeFirst()
                continue
            }

            when (child) {
                is TextView -> {
                    val widthLeft = maxWidth - width
                    // what if the width is smaller than a word/char?
                    val newTextStartIndex = getTextBreak(child.text, child.paint, widthLeft.toFloat())
                    // TODO: Don't actually need to create a text view
                    virtualChildren.add(0, child.makeSimpleCopy().apply {
                        text = child.text.substring(newTextStartIndex)
                    })
                }
            }
            // Move to next row
            height += rowMaxHeight
            width = 0
            rowMaxHeight = 0

            if (height > maxHeight) {
                // The height already exceeds the maximum height, don't need to continue checking
                return maxHeight
            }

            virtualChildren.removeFirst()
        }

        // Add in the last row
        height += rowMaxHeight
        return height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Considerations:
        // Gravity? No, only align by baseline
        // Baseline
        // margin and padding
        // layout params (match parent)
        // MeasureSpec
        // visibility
        val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)

        children.forEach {
            // Ignore invisible children
            if (it.visibility == GONE) {
                return
            }

            // Find out how big the children views want to be
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }

        // Determine the measured width
        val totalWidth = children.sumOf { it.measuredWidth }
        val width = when (specWidthMode) {
            MeasureSpec.UNSPECIFIED -> {
                // If the width is unspecified, stack all the views horizontally
                totalWidth
            }
            MeasureSpec.EXACTLY -> {
                // No choice but to follow the width. The last view will probably be stretched out
                specWidth
            }
            else -> {
                // Use as little width as possible.
                // Fit all the views before the specWidth
                min(totalWidth, specWidth)
            }
        }

        // Determine the measured height
        val height = if (specHeightMode == MeasureSpec.EXACTLY) {
            // No choice
            specHeight
        } else if (specWidthMode == MeasureSpec.UNSPECIFIED) {
            // Height seems to be unaffected by baseline alignment TODO: check this
            // it seems to only be affected if there is gravity which this layout will not support
            // Therefore, just take the maximum height of the views
            children.maxOf { it.measuredHeight }
        } else {
            // The width has to be at most/exactly some value
            // So just measure what the height is
            measureHeight(children, specWidth, specHeight)
        }

        var x = ""
        x += "spec\nheight: $specHeight, width: $specWidth\n\n"
        children.forEachIndexed { index, it ->
            x += "child$index:\n"
            x += "width: ${it.measuredWidth}\n"
            x += "height: ${it.measuredHeight}\n\n"
        }

        x += "measured: $width, $height"
        setMeasuredDimension(width, height)
    }
}

@Preview
@Composable
private fun TextBlockLayoutPreview() {
    AndroidView(factory = {
        val layout = TextBlockLayout(it)
        val wrapContent = { ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT) }
        layout.layoutParams = wrapContent()
        val tv1 = TextView(it).apply {
            layoutParams = wrapContent()
            text = "hello world!"
        }
        val tv2 = TextView(it).apply {
            layoutParams = wrapContent()
            text = "hello world!"
        }

        layout.addView(tv1)
        layout.addView(tv2)

        layout
    })
}