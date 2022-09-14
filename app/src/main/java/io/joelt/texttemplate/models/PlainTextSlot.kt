package io.joelt.texttemplate.models

class PlainTextSlot : Slot {
    override val placeholderText = "Text"
    var value: String = ""

    override fun valueToString(): String = value

    override fun setValueFromString(s: String) {
        value = s
    }
}