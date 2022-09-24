package io.joelt.texttemplate.ui.components

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

private class TemplateTextView : View {
    val textView = TextView(context)
    val layout = LinearLayout(context).apply {
        addView(textView)
    }
    var text: String = ""
        set(value) {
            field = value
            textView.text = value
            invalidate()
        }

    constructor(context: Context, text: String) : super(context) {
        this.text = text
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // TODO: Implement Constructor
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        // TODO: Implement Constructor
    }

    override fun onDraw(canvas: Canvas) {
        layout.layout(0, 0, width, height)
        layout.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layout.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(layout.measuredWidth, layout.measuredHeight)
    }
}

@Composable
fun TemplateText(text: String) {
    AndroidView(factory = {
        TemplateTextView(it, text)
    })
}

@Preview
@Composable
fun SampleText() {
    TemplateText("Hello world!")
}