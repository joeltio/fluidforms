package io.joelt.texttemplate.ui.views

class ViewsLayout {
    var measuredWidth: Int = 0
    var measuredHeight: Int = 0
    var measuredEnd: ViewPosition = ViewPosition(0, 0)

    private val views = HashMap<Int, ViewPosition>()

    fun setViewAt(index: Int, position: ViewPosition) {
        views[index] = position
    }

    fun viewAt(index: Int): ViewPosition? {
        return views[index]
    }
}