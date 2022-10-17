package io.joelt.texttemplate.ui.views.breakable

import android.view.View.MeasureSpec
import android.widget.TextView

/**
 * Calculates where (the index of the CharSequence) to break the text.
 *
 * @param view The TextView to calculate
 * @param availableSpace the amount of space to fit the text
 * @return the index of location to split. Characters of index [0, index) will
 * fit into the space. If it is not possible to fit any characters, returns a 0
 */
fun breakText(view: TextView, availableSpace: Int): Int {
    if (view.text.isEmpty()) {
        return 0
    }

    // Check if we can even fit the first letter
    val minWidth = view.paint.measureText(view.text[0].toString())
    if (availableSpace < minWidth) {
        // There isn't enough space to display any characters
        return 0
    }

    view.measure(
        MeasureSpec.makeMeasureSpec(availableSpace, MeasureSpec.AT_MOST),
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    )
    return view.layout.getLineEnd(0)
}
