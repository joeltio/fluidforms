package io.joelt.texttemplate.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.children

class TextBlockLayout : ViewGroup {
    private lateinit var layoutCache: ViewsLayout

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!changed) {
            return
        }

        children.forEachIndexed { index, view ->
            if (view.visibility == View.GONE) {
                return@forEachIndexed
            }

            val position = layoutCache.viewAt(index)!!
            view.layout(
                left + position.left,
                top + position.top,
                left + position.left + view.measuredWidth,
                top + position.top + view.measuredHeight,
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layoutCache = layoutViewsWrap(children, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(layoutCache.measuredWidth, layoutCache.measuredHeight)
    }
}

@Preview(showBackground = true)
@Composable
private fun TextBlockLayoutPreview() {
    AndroidView(factory = {
        val layout = TextBlockLayout(it)
        val tv1 = TextView(it).apply {
            text = "hello world!"
            setTextColor(Color.RED)
        }
        val tv2 = TextView(it).apply {
            text = "hello world!"
            setTextColor(Color.GREEN)
        }
        val tv3 = TextView(it).apply {
            text = "hello world!"
            setTextColor(Color.BLUE)
        }

        layout.addView(tv1)
        layout.addView(tv2)
        layout.addView(tv3)

        layout
    })
}