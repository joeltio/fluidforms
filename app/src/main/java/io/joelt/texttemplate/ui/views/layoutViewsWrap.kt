package io.joelt.texttemplate.ui.views

import android.view.View
import android.view.View.MeasureSpec
import io.joelt.texttemplate.ui.views.breakable.Breakable
import kotlin.math.max

/**
 * Lay out views like words in a paragraph, and wrap the views around to the
 * next line when the line is filled.
 *
 * @param views sequence of views to lay out
 * @param widthMeasureSpec width measure spec to comply to
 * @param heightMeasureSpec height measure spec to comply to
 * @param startOffset the top and left offset for the first row's views
 * @return The positions of the children and the size of the layout
 */
fun layoutViewsWrap(
    views: Sequence<View>,
    widthMeasureSpec: Int,
    heightMeasureSpec: Int,
    startOffset: ViewPosition = ViewPosition(0, 0)
): ViewsLayout {
    val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
    val specWidth = MeasureSpec.getSize(widthMeasureSpec)
    val specHeightMode = MeasureSpec.getMode(heightMeasureSpec)
    val specHeight = MeasureSpec.getSize(heightMeasureSpec)

    val viewsLayout = ViewsLayout()
    var offset = startOffset

    // Check if we can just stack all the way, horizontally
    if (specWidthMode == MeasureSpec.UNSPECIFIED) {
        var maxHeight = 0
        views.forEachIndexed { index, view ->
            if (view.visibility == View.GONE) {
                return@forEachIndexed
            }

            view.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                heightMeasureSpec
            )

            // Update the views layout and offset
            viewsLayout.setViewAt(index, offset.copy())
            viewsLayout.measuredWidth += view.measuredWidth

            offset = offset.copy(left = offset.left + view.measuredWidth)
            maxHeight = max(maxHeight, view.measuredHeight)
        }
        viewsLayout.measuredHeight = maxHeight
        viewsLayout.measuredEnd = offset.copy()

        return viewsLayout
    }

    // There is some constraint for the width
    var rowMaxHeight = 0
    var maxWidth = 0

    views.forEachIndexed { index, view ->
        if (view.visibility == View.GONE) {
            return@forEachIndexed
        }

        // See how big the view wants to be
        view.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        )

        val widthLeft = specWidth - offset.left
        if (view.measuredWidth <= widthLeft) {
            // There is enough room for the view on the row
            viewsLayout.setViewAt(index, offset.copy())

            offset = offset.copy(left = offset.left + view.measuredWidth)
            rowMaxHeight = max(rowMaxHeight, view.measuredHeight)

            return@forEachIndexed
        }

        // Check if we can split the view
        if (view is Breakable) {
            view.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                offset.left,
                rowMaxHeight
            )
            // Breakable views will be flushed to the left so that it can
            // put their broken views at the start of the next line
            viewsLayout.setViewAt(index, ViewPosition(0, offset.top))

            offset = view.measuredEnd.copy(
                top = offset.top + view.measuredEnd.top)
            // Update the rowMaxHeight
            rowMaxHeight = view.rowMaxHeight
            maxWidth = max(maxWidth, view.measuredWidth)

            return@forEachIndexed
        }

        // View cannot be split, try using the next row
        if (view.measuredWidth > specWidth) {
            // The view is bigger than the maximum width. Remeasure with
            // stricter rules so it fits
            view.measure(
                // TODO(Optimise so that all children measure with
                //  widthMeasureSpec and there wouldn't be a need to re-measure)
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        maxWidth = max(maxWidth, offset.left - startOffset.left)

        if (offset.left != 0) {
            // Move the offset to the next row
            offset = ViewPosition(0,
                offset.top + rowMaxHeight
            )
            rowMaxHeight = 0
        }

        viewsLayout.setViewAt(index, offset.copy())
        offset = offset.copy(left = offset.left + view.measuredWidth)
        rowMaxHeight = view.measuredHeight
        maxWidth = max(maxWidth, offset.left - startOffset.left)
    }
    maxWidth = max(maxWidth, offset.left - startOffset.left)

    viewsLayout.measuredWidth = maxWidth
    viewsLayout.measuredHeight = offset.top + rowMaxHeight
    viewsLayout.measuredEnd = offset.copy()

    if (specHeightMode == MeasureSpec.UNSPECIFIED
        || specHeight >= viewsLayout.measuredHeight) {
        return viewsLayout
    }

    // The layout exceeds the height. Need to reallocate the height evenly
    var measuredEnd = startOffset
    views.forEachIndexed { index, view ->
        if (view.visibility == View.GONE) {
            return@forEachIndexed
        }

        val viewMaxHeight = view.measuredHeight / viewsLayout.measuredHeight * specHeight
        view.measure(
            // Fix the width
            MeasureSpec.makeMeasureSpec(view.measuredWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewMaxHeight, MeasureSpec.AT_MOST),
        )

        val initialPosition = viewsLayout.viewAt(index)!!
        val viewTopPos = initialPosition.top + (
                (initialPosition.top - startOffset.top) / viewsLayout.measuredHeight * specHeight)
        viewsLayout.setViewAt(index, initialPosition.copy(top = viewTopPos))

        // TODO: (This may be expensive, could be optimised to only run for the
        //  last index)
        measuredEnd = ViewPosition(
            initialPosition.left + view.measuredWidth,
            viewTopPos
        )
    }

    viewsLayout.measuredHeight = specHeight
    viewsLayout.measuredEnd = measuredEnd

    return viewsLayout
}