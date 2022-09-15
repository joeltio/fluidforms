package io.joelt.texttemplate.models

class PlainTextSlot(var value: String) : Slot {
    override val placeholderText = "Text"

    override fun valueToString(): String = value

    override fun setValueFromString(s: String) {
        value = s
    }
}